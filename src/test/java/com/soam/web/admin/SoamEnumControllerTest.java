package com.soam.web.admin;

import com.soam.model.priority.PriorityType;
import com.soam.service.soamenum.SoamEnumService;
import com.soam.web.ModelConstants;
import com.soam.web.ViewConstants;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoamEnumController.class)
class SoamEnumControllerTest {
    private final static PriorityType TEST_PRIORITY_1 = new PriorityType();
    private final static PriorityType TEST_PRIORITY_2 = new PriorityType();

    private static final String URL_VIEW_SOAM_ENUM_LIST = "/admin/soamEnum/list";

    static {
        TEST_PRIORITY_1.setId(1);
        TEST_PRIORITY_1.setName("Test Priority 1");
        TEST_PRIORITY_1.setSequence(10);
        TEST_PRIORITY_2.setId(2);
        TEST_PRIORITY_2.setName("Test Priority 2");
        TEST_PRIORITY_2.setSequence(20);
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SoamEnumService soamEnumService;

    @BeforeEach
    void setup() {
        given(soamEnumService.findAll()).willReturn(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2));
        given(soamEnumService.findByType(PriorityType.class)).willReturn(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2));
    }

    @Test
    void testListAll() throws Exception {
        mockMvc.perform(get(URL_VIEW_SOAM_ENUM_LIST))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_ADMIN_SOAM_ENUMS))
                .andExpect(model().attribute(ModelConstants.ATTR_ADMIN_SOAM_ENUMS, new IsEqual<>(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2))))
                .andExpect(model().attributeExists(ModelConstants.ATTR_ADMIN_SOAM_ENUM_TYPES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_ADMIN_SOAM_ENUM_FORM))
                .andExpect(view().name(ViewConstants.VIEW_ADMIN_SOAM_EMUM_LIST));

        mockMvc.perform(post(URL_VIEW_SOAM_ENUM_LIST)
                        .param("filterSoamEnumType", String.valueOf(SoamEnumType.PriorityType.ordinal())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(ModelConstants.ATTR_ADMIN_SOAM_ENUMS))
                .andExpect(model().attribute(ModelConstants.ATTR_ADMIN_SOAM_ENUMS, new IsEqual<>(List.of(TEST_PRIORITY_1, TEST_PRIORITY_2))))
                .andExpect(model().attributeExists(ModelConstants.ATTR_ADMIN_SOAM_ENUM_TYPES))
                .andExpect(model().attributeExists(ModelConstants.ATTR_ADMIN_SOAM_ENUM_FORM))
                .andExpect(view().name(ViewConstants.VIEW_ADMIN_SOAM_EMUM_LIST));
    }
}
