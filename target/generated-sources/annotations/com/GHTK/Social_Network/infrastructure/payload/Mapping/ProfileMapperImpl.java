package com.GHTK.Social_Network.infrastructure.payload.Mapping;

import com.GHTK.Social_Network.domain.entity.user.User;
import com.GHTK.Social_Network.infrastructure.payload.dto.ProfileDto;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-07-22T10:25:59+0700",
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
        user.avatar( profile.getAvatar() );
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

        ProfileDto.ProfileDtoBuilder profileDto = ProfileDto.builder();

        profileDto.profileId( user.getUserId() );
        profileDto.lastName( user.getLastName() );
        profileDto.firstName( user.getFirstName() );
        profileDto.userEmail( user.getUserEmail() );
        profileDto.avatar( user.getAvatar() );
        profileDto.dob( user.getDob() );
        profileDto.phoneNumber( user.getPhoneNumber() );
        profileDto.homeTown( user.getHomeTown() );
        profileDto.schoolName( user.getSchoolName() );
        profileDto.workPlace( user.getWorkPlace() );
        profileDto.isProfilePublic( user.getIsProfilePublic() );

        return profileDto.build();
    }
}
