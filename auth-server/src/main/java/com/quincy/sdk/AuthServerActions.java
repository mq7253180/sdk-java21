package com.quincy.sdk;

import java.util.List;

import com.quincy.auth.entity.Permission;
import com.quincy.auth.entity.Role;
import com.quincy.sdk.o.Menu;
import com.quincy.sdk.o.User;

public interface AuthServerActions {
	public abstract Object userExt(User user);
	public abstract void sms(String mobilePhone, String vcode, int expireMinuts);
	public abstract List<Role> findRoles(Long userId);
	public abstract List<Permission> findPermissions(Long userId);
	public abstract List<Menu> findMenus(Long userId);
}