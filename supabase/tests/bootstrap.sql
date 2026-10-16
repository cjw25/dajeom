-- CI-only database roles. Never run this file in your Supabase project.
create role anon nologin;
create role authenticated nologin;
create role service_role nologin bypassrls;
