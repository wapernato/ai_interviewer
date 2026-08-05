ALTER TABLE admin_role_change_audit
    ADD COLUMN actor_username varchar(100),
    ADD COLUMN actor_email varchar(100),
    ADD COLUMN target_username varchar(100),
    ADD COLUMN target_email varchar(100);

UPDATE admin_role_change_audit audit
SET actor_username = actor.username,
    actor_email = actor.email,
    target_username = target.username,
    target_email = target.email
FROM users actor,
     users target
WHERE audit.actor_user_id = actor.id
  AND audit.target_user_id = target.id;

ALTER TABLE admin_role_change_audit
    ALTER COLUMN actor_username SET NOT NULL,
    ALTER COLUMN actor_email SET NOT NULL,
    ALTER COLUMN target_username SET NOT NULL,
    ALTER COLUMN target_email SET NOT NULL;
