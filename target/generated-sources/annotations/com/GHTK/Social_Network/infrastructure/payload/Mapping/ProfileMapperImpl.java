package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-16T15:28:29+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public User profileToUser(ProfileDto profile) {
        if ( profile == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userId( profile.getProfileId() );
        user.firstName( profile.getFirstName() );
        user.lastName( profile.getLastName() );
        user.userEmail( profile.getUserEmail() );
        user.dob( profile.getDob() );
        user.phoneNumber( profile.getPhoneNumber() );
        user.homeTown( profile.getHomeTown() );
        user.schoolName( profile.getSchoolName() );
        user.workPlace( profile.getWorkPlace() );
        user.isProfilePublic( profile.getIsProfilePublic() );

        return user.build();
    }

    @Override
    public ProfileDto userToProfileDto(User user) {
        if ( user == null ) {
            return null;
        }

        ProfileDto profileDto = new ProfileDto();

        profileDto.setProfileId( user.getUserId() );
        profileDto.setLastName( user.getLastName() );
        profileDto.setFirstName( user.getFirstName() );
        profileDto.setUserEmail( user.getUserEmail() );
        profileDto.setDob( user.getDob() );
        profileDto.setPhoneNumber( user.getPhoneNumber() );
        profileDto.setHomeTown( user.getHomeTown() );
        profileDto.setSchoolName( user.getSchoolName() );
        profileDto.setWorkPlace( user.getWorkPlace() );
        profileDto.setIsProfilePublic( user.getIsProfilePublic() );

        return profileDto;
    }
}
