package ureka.team3.utong_admin.groupcode.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.groupcode.dto.GroupCodeDto;
import ureka.team3.utong_admin.groupcode.service.GroupCodeServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupCodeController.class)
@WithMockUser
class GroupCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupCodeServiceImpl groupCodeService;

    @Test
    void listGroupCode_Success() throws Exception {
        // given
        int pageNumber = 0;
        int pageSize = 10;

        List<GroupCodeDto> groupCodeList = Arrays.asList(
                GroupCodeDto.builder()
                        .groupCode("001")
                        .groupCodeName("테스트 그룹1")
                        .groupCodeDesc("테스트 설명1")
                        .build(),
                GroupCodeDto.builder()
                        .groupCode("002")
                        .groupCodeName("테스트 그룹2")
                        .groupCodeDesc("테스트 설명2")
                        .build()
        );

        ApiResponse<List<GroupCodeDto>> expectedResponse = ApiResponse.success(groupCodeList);
        when(groupCodeService.listGroupCode(pageNumber, pageSize)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/group-codes")
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(groupCodeService, times(1)).listGroupCode(pageNumber, pageSize);
    }

    @Test
    void detailGroupCode_Success() throws Exception {
        // given
        String groupCode = "001";
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode(groupCode)
                .groupCodeName("테스트 그룹")
                .groupCodeDesc("테스트 설명")
                .build();

        ApiResponse<GroupCodeDto> expectedResponse = ApiResponse.success(groupCodeDto);
        when(groupCodeService.detailGroupCode(groupCode)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/group-codes/{groupCode}", groupCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"))
                .andExpect(jsonPath("$.data.groupCode").value(groupCode))
                .andExpect(jsonPath("$.data.groupCodeName").value("테스트 그룹"))
                .andExpect(jsonPath("$.data.groupCodeDesc").value("테스트 설명"));

        verify(groupCodeService, times(1)).detailGroupCode(groupCode);
    }

    @Test
    void countGroupCodes_Success() throws Exception {
        // given
        Long count = 5L;
        ApiResponse<Long> expectedResponse = ApiResponse.success(count);
        when(groupCodeService.countGroupCode()).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/group-codes/count")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(count));

        verify(groupCodeService, times(1)).countGroupCode();
    }

    @Test
    void createGroupCode_Success() throws Exception {
        // given
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode("003")
                .groupCodeName("새로운 그룹")
                .groupCodeDesc("새로운 그룹 설명")
                .build();

        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(groupCodeService.createGroupCode(any(GroupCodeDto.class))).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/api/admin/group-codes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupCodeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"));

        verify(groupCodeService, times(1)).createGroupCode(any(GroupCodeDto.class));
    }

    @Test
    void updateGroupCode_Success() throws Exception {
        // given
        String groupCode = "001";
        GroupCodeDto groupCodeDto = GroupCodeDto.builder()
                .groupCode(groupCode)
                .groupCodeName("수정된 그룹")
                .groupCodeDesc("수정된 그룹 설명")
                .build();

        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(groupCodeService.updateGroupCode(eq(groupCode), any(GroupCodeDto.class))).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(put("/api/admin/group-codes/{groupCode}", groupCode)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupCodeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"));

        verify(groupCodeService, times(1)).updateGroupCode(eq(groupCode), any(GroupCodeDto.class));
    }

    @Test
    void deleteGroupCode_Success() throws Exception {
        // given
        String groupCode = "001";
        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(groupCodeService.deleteGroupCode(groupCode)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(delete("/api/admin/group-codes/{groupCode}", groupCode)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"));

        verify(groupCodeService, times(1)).deleteGroupCode(groupCode);
    }

}