drop index if exists uk_ai_profiles_single_active;

create index if not exists idx_ai_profiles_active
    on ai_profiles (active);
