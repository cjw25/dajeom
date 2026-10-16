-- Run in Supabase SQL Editor. Only the Java server's secret key can access this data.
begin;
create table if not exists public.dajeom_state (
  key text primary key,
  value jsonb not null,
  expires_at timestamptz not null
);
create index if not exists dajeom_state_expiry on public.dajeom_state(expires_at);
alter table public.dajeom_state enable row level security;
revoke all on public.dajeom_state from public, anon, authenticated;
grant select, insert, update, delete on public.dajeom_state to service_role;

create or replace function public.dajeom_lock_acquire(p_key text,p_owner text,p_seconds integer)
returns boolean language plpgsql security invoker set search_path=public,pg_temp as $$
begin
  if p_seconds < 1 or p_seconds > 300 then raise exception 'Invalid lease duration'; end if;
  insert into public.dajeom_state(key,value,expires_at)
  values(p_key,jsonb_build_object('owner',p_owner),now()+make_interval(secs=>p_seconds))
  on conflict(key) do update set value=excluded.value,expires_at=excluded.expires_at
  where dajeom_state.expires_at<=now();
  return found;
end;
$$;

create or replace function public.dajeom_state_commit(p_key text,p_value jsonb,p_expires_at timestamptz,p_lock_key text,p_owner text)
returns boolean language plpgsql security invoker set search_path=public,pg_temp as $$
begin
  perform 1 from public.dajeom_state
    where key=p_lock_key and value->>'owner'=p_owner and expires_at>clock_timestamp() for update;
  if not found then return false; end if;
  insert into public.dajeom_state(key,value,expires_at) values(p_key,p_value,p_expires_at)
  on conflict(key) do update set value=excluded.value,expires_at=excluded.expires_at;
  return true;
end;
$$;

create or replace function public.dajeom_lock_release(p_key text,p_owner text)
returns void language sql security invoker set search_path=public,pg_temp as $$
  delete from public.dajeom_state where key=p_key and value->>'owner'=p_owner;
$$;

create or replace function public.dajeom_state_delete(p_key text,p_lock_key text,p_owner text)
returns boolean language plpgsql security invoker set search_path=public,pg_temp as $$
begin
  perform 1 from public.dajeom_state
    where key=p_lock_key and value->>'owner'=p_owner and expires_at>clock_timestamp() for update;
  if not found then return false; end if;
  delete from public.dajeom_state where key=p_key;
  return true;
end;
$$;

create or replace function public.dajeom_expired_state_cleanup()
returns void language sql security invoker set search_path=public,pg_temp as $$
  delete from public.dajeom_state where expires_at<=now();
$$;

create or replace function public.dajeom_budget_consume(p_key text,p_limit integer)
returns boolean language plpgsql security invoker set search_path=public,pg_temp as $$
begin
  if p_limit < 1 or p_limit > 10000 then raise exception 'Invalid budget'; end if;
  insert into public.dajeom_state(key,value,expires_at)
  values(p_key,jsonb_build_object('count',1),now()+interval '2 days')
  on conflict(key) do update set value=jsonb_build_object('count',(dajeom_state.value->>'count')::integer+1)
  where (dajeom_state.value->>'count')::integer<p_limit;
  return found;
end;
$$;

revoke all on function public.dajeom_lock_acquire(text,text,integer) from public,anon,authenticated;
revoke all on function public.dajeom_state_commit(text,jsonb,timestamptz,text,text) from public,anon,authenticated;
revoke all on function public.dajeom_lock_release(text,text) from public,anon,authenticated;
revoke all on function public.dajeom_expired_state_cleanup() from public,anon,authenticated;
grant execute on function public.dajeom_lock_acquire(text,text,integer) to service_role;
grant execute on function public.dajeom_state_commit(text,jsonb,timestamptz,text,text) to service_role;
grant execute on function public.dajeom_lock_release(text,text) to service_role;
revoke all on function public.dajeom_state_delete(text,text,text) from public,anon,authenticated;
grant execute on function public.dajeom_state_delete(text,text,text) to service_role;
grant execute on function public.dajeom_expired_state_cleanup() to service_role;
revoke all on function public.dajeom_budget_consume(text,integer) from public,anon,authenticated;
grant execute on function public.dajeom_budget_consume(text,integer) to service_role;
commit;
