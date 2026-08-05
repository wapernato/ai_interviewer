
CREATE TABLE IF NOT EXISTS admin_role_change_audit (
	id bigserial primary key,
	actor_user_id bigint NOT NULL,
	target_user_id bigint NOT NULL,
	old_role varchar(30) NOT NULL,
	new_role varchar(30) NOT NULL,
	changed_at timestamp NOT NULL DEFAULT current_timestamp,

	CONSTRAINT fk_admin_role_change_audit_actor
		foreign KEY (actor_user_id) REFERENCES users(id),

	CONSTRAINT fk_admin_role_change_audit_target
		foreign KEY (target_user_id) REFERENCES users(id)

);
