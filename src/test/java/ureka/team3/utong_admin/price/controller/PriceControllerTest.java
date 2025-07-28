package ureka.team3.utong_admin.price.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ureka.team3.utong_admin.common.dto.ApiResponse;
import ureka.team3.utong_admin.price.dto.PriceDto;
import ureka.team3.utong_admin.price.service.PriceServiceImpl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PriceController.class)
@WithMockUser
class PriceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PriceServiceImpl priceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void updatePrice_성공_test() throws Exception {
        // given
        PriceDto priceDto = PriceDto.builder()
                .minimumPrice(1000L)
                .minimumRate(30.0F)
                .tax(2.5F)
                .build();

        ApiResponse<Void> expectedResponse = ApiResponse.success(null);
        when(priceService.updatePrice(any(), any())).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(put("/api/admin/prices")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(priceDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"));

    }

    @Test
    void getPrice_성공_test() throws Exception {
        // given
        String id = "903ee67c-71b3-432e-bbd1-aaf5d5043376";
        PriceDto priceDto = PriceDto.builder()
                .minimumPrice(1000L)
                .minimumRate(30.0F)
                .tax(2.5F)
                .build();

        ApiResponse<PriceDto> expectedResponse = ApiResponse.success(priceDto);
        when(priceService.getPrice(id)).thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(get("/api/admin/prices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200"))
                .andExpect(jsonPath("$.data.minimumPrice").value(1000L))
                .andExpect(jsonPath("$.data.minimumRate").value(30.0F))
                .andExpect(jsonPath("$.data.tax").value(2.5F));
    }
}