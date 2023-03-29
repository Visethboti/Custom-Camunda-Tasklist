package com.example.customcamundatasklist.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class AuthRoleService {
	public ArrayList<String> getRealmRoles(Authentication auth) {
		Iterator<? extends GrantedAuthority> authItr = auth.getAuthorities().iterator();
		ArrayList<String> userRealmRoles = new ArrayList<String>();
		while (authItr.hasNext()) {
			String tempRole = authItr.next().toString();
			if (tempRole.contains("realm-role")) {
				userRealmRoles.add(tempRole);
			}
		}
		return userRealmRoles;
	}

	public ArrayList<String> getUserRoleGroups(Authentication auth) {
		ArrayList<String> userRealmRoles = getRealmRoles(auth);
		for (int i = 0; i < userRealmRoles.size(); i++) {
			userRealmRoles.set(i, userRealmRoles.get(i).substring(16));
		}
		return userRealmRoles;
	}
}
