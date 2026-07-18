create unique index uk_ai_profiles_single_active
    on ai_profiles (active)
    where active is true;
