-- Executed against a disposable PostgreSQL database in CI after the migration.
do $$
begin
  assert not has_table_privilege('anon','public.dajeom_state','SELECT');
  assert not has_table_privilege('authenticated','public.dajeom_state','SELECT');
  assert not has_function_privilege('anon','public.dajeom_lock_acquire(text,text,integer)','EXECUTE');
  assert not has_function_privilege('authenticated','public.dajeom_budget_consume(text,integer)','EXECUTE');
end;
$$;
set role service_role;
do $$
begin
  assert public.dajeom_lock_acquire('lock:test','one',300);
  assert not public.dajeom_lock_acquire('lock:test','two',300);
  assert not public.dajeom_state_commit('session:test','{"step":1}',now()+interval '1 hour','lock:test','two');
  assert public.dajeom_state_commit('session:test','{"step":1}',now()+interval '1 hour','lock:test','one');
  perform public.dajeom_lock_release('lock:test','two');
  assert exists(select 1 from public.dajeom_state where key='lock:test');
  update public.dajeom_state set expires_at=now()-interval '1 second' where key='lock:test';
  assert public.dajeom_lock_acquire('lock:test','two',300);
  assert not public.dajeom_state_commit('session:test','{"step":99}',now()+interval '1 hour','lock:test','one');
  assert (select value->>'step' from public.dajeom_state where key='session:test')='1';
  assert not public.dajeom_state_delete('session:test','lock:test','one');
  assert public.dajeom_budget_consume('budget:test',1);
  assert not public.dajeom_budget_consume('budget:test',1);
  insert into public.dajeom_state values('expired:test','{}',now()-interval '1 second');
  perform public.dajeom_expired_state_cleanup();
  assert not exists(select 1 from public.dajeom_state where key='expired:test');
  perform public.dajeom_lock_release('lock:test','two');
end;
$$;
reset role;
