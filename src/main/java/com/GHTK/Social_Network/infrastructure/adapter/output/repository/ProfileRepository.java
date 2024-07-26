package com.GHTK.Social_Network.infrastructure.adapter.output.repository;

import com.GHTK.Social_Network.infrastructure.adapter.output.entity.entity.user.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
  @Modifying
  @Query("""
              update ProfileEntity p
              set p.isDobPublic = :#{#request.isDobPublic},
                p.isPhoneNumberPublic = :#{#request.isPhoneNumberPublic} ,
                p.isHomeTownPublic = :#{#request.isHomeTownPublic},
                p.isSchoolNamePublic = :#{#request.isSchoolNamePublic},
                p.isWorkPlacePublic = :#{#request.isWorkPlacePublic}
              where p.userId = :userId
          """)
  int updateProfilePrivacy(ProfileEntity request, Long userId);
}
