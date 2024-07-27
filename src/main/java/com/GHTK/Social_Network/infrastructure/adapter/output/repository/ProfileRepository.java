package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
  @Modifying
  @Transactional
  @Query("""
        update ProfileEntity p
        set p.isDobPublic = :isDobPublic,
            p.isPhoneNumberPublic = :isPhoneNumberPublic,
            p.isHomeTownPublic = :isHomeTownPublic,
            p.isSchoolNamePublic = :isSchoolNamePublic,
            p.isWorkPlacePublic = :isWorkPlacePublic
        where p.userId = :userId
    """)
  int updateProfilePrivacy(
          boolean isDobPublic,
          boolean isPhoneNumberPublic,
          boolean isHomeTownPublic,
          boolean isSchoolNamePublic,
          boolean isWorkPlacePublic,
          Long userId
  );
}