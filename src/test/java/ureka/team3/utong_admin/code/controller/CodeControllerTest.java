package ureka.team3.utong_admin.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ureka.team3.utong_admin.code.dto.CodeDto;
import ureka.team3.utong_admin.code.service.CodeServiceImpl;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.groupcode.service.GroupCodeServiceImpl;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CodeController.class)
@WithMockUser
class CodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CodeServiceImpl codeService;

    @Test
    void listCode_성공_Test() throws Exception {
        // given
        String groupCode = "010";
        int pageNumber = 0;
        int pageSize = 10;

        List<CodeDto> codeList = List.of(
                CodeDto.builder()
                        .groupCode(groupCode)
                        .code("001")
                        .codeName("테스트 코드1")
                        .codeNameBrief("테스트 설명1")
                        .orderNo(1)
                        .build(),
                CodeDto.builder()
                        .groupCode(groupCode)
                        .code("002")
                        .codeName("테스트 코드2")
                        .codeNameBrief("테스트 설명2")
                        .orderNo(2)
                        .build()
        );

        ApiResponse<List<CodeDto>> expectedResponse = ApiResponse.success(codeList);
        when(codeService.listCode(groupCode, pageNumber, pageSize))
                .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/codes")
                .param("groupCode", groupCode)
                .param("pageNumber", String.valueOf(pageNumber))
                .param("pageSize", String.valueOf(pageSize)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(codeService, times(1)).listCode(groupCode, pageNumber, pageSize);
    }

    @Test
    void detailCode_성공_Test() throws Exception {
        // given
        String groupCode = "010";
        String code = "001";

        CodeDto codeDto = CodeDto.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName("테스트 코드1")
                .codeNameBrief("테스트 설명1")
                .orderNo(1)
                .build();

        ApiResponse<CodeDto> expectedResponse = ApiResponse.success(codeDto);
        when(codeService.detailCode(any())).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/codes/{groupCode}/{code}", groupCode, code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"))
                .andExpect(jsonPath("$.data.code").value(code));

        verify(codeService, times(1)).detailCode(any());
    }

    @Test
    void createCode_성공_Test() throws Exception {
        // given
        CodeDto codeDto = CodeDto.builder()
                .groupCode("010")
                .code("003")
                .codeName("테스트 코드3")
                .codeNameBrief("테스트 설명3")
                .orderNo(3)
                .build();

        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(codeService.createCode(any())).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/api/admin/codes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"));

        verify(codeService, times(1)).createCode(any());
    }

    @Test
    void updateCode_성공_Test() throws Exception {
        // given
        String groupCode = "010";
        String code = "001";

        CodeDto codeDto = CodeDto.builder()
                .groupCode(groupCode)
                .code(code)
                .codeName("업데이트된 코드")
                .codeNameBrief("업데이트된 설명")
                .orderNo(1)
                .build();

        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(codeService.updateCode(any(), any())).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(put("/api/admin/codes/{groupCode}/{code}", groupCode, code)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(codeDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"));

        verify(codeService, times(1)).updateCode(any(), any());
    }

    @Test
    void deleteCode_성공_Test() throws Exception {
        // given
        String groupCode = "010";
        String code = "001";

        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(codeService.deleteCode(any())).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(delete("/api/admin/codes/{groupCode}/{code}", groupCode, code)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"));

        verify(codeService, times(1)).deleteCode(any());
    }

    @Test
    void countCode_성공_Test() throws Exception {
        // given
        long count = 100L;
        ApiResponse<Long> expectedResponse = ApiResponse.success(count);
        when(codeService.countCode()).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/codes/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(200))
                .andExpect(jsonPath("$.codeName").value("SUCCESS"))
                .andExpect(jsonPath("$.data").value(count));

        verify(codeService, times(1)).countCode();
    }
}