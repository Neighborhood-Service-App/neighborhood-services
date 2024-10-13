package com.neighborhoodservice.user.dto;

import com.neighborhoodservice.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserPatchMapper {

    void updateUserFromDto(UserPatchRequest patchRequest, @MappingTarget User user);

}
