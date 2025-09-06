package main.java.com.tiktok.demo.dto.response;

import java.util.List;

import com.tiktok.demo.dto.response.UserPublicResponse;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRelationPageResponse {
    List<UserPublicResponse> followedUsers;
    int nextPage;
    boolean hasMore;
}
