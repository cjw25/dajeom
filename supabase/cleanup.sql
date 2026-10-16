-- Enable Supabase Cron (pg_cron) in the dashboard first, then run once in SQL Editor.
-- Keeps expired birth details, concerns and transcripts out of the live table.
select cron.schedule('dajeom-expired-state-cleanup','*/10 * * * *',
  $$select public.dajeom_expired_state_cleanup();$$)
where not exists(select 1 from cron.job where jobname='dajeom-expired-state-cleanup');
