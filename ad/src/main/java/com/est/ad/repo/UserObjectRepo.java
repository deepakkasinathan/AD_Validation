package com.est.ad.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.est.ad.entity.UserObjects;

public interface UserObjectRepo extends JpaRepository<UserObjects, String> {

	@Query("SELECT u FROM UserObjects u WHERE u.object_name = :objectName AND u.object_type = :objectType")
	UserObjects findByObjectName(@Param("objectName") String objectName,
	                             @Param("objectType") String objectType);

}