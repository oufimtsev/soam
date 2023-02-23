package com.soam.web.admin;

import com.soam.model.priority.PriorityType;
import com.soam.service.EntityNotFoundException;
import com.soam.service.soamenum.SoamEnumService;
import com.soam.web.ModelConstants;
import com.soam.web.RedirectConstants;
import com.soam.web.SoamFormController;
import com.soam.web.ViewConstants;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SoamEnumFormController.class)
public class SoamEnumFormControllerTest {
    private final static PriorityType TEST_PRIORITY_1 = new PriorityType();
    private final static PriorityType TEST_PRIORITY_2 = new PriorityType();

    private static final int EMPTY_SOAM_ENUM_ID = 99;

    private static final String URL_EDIT_SOAM_ENUM = "/admin/soamEnum/{soamEnumId}/edit";

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
        given(soamEnumService.getById(TEST_PRIORITY_1.getId())).willReturn(TEST_PRIORITY_1);
        given(soamEnumService.getById(EMPTY_SOAM_ENUM_ID)).willThrow(new EntityNotFoundException("Soam Enum", EMPTY_SOAM_ENUM_ID));
        given(soamEnumService.findBySoamEnumIdAndName(PriorityType.class, TEST_PRIORITY_2.getName())).willReturn(Optional.of(TEST_PRIORITY_2));
        given(soamEnumService.findBySoamEnumIdAndSequence(PriorityType.class, TEST_PRIORITY_2.getSequence())).willReturn(Optional.of(TEST_PRIORITY_2));
    }

    @Test
    void testInitUpdateForm() throws Exception {
        mockMvc.perform(get(URL_EDIT_SOAM_ENUM, TEST_PRIORITY_1.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attribute(ModelConstants.ATTR_ADMIN_SOAM_ENUM, new IsEqual<>(TEST_PRIORITY_1)))
                .andExpect(view().name(ViewConstants.VIEW_ADMIN_SOAM_EMUM_UPDATE_FORM));

        mockMvc.perform(get(URL_EDIT_SOAM_ENUM, EMPTY_SOAM_ENUM_ID))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists(SoamFormController.FLASH_DANGER))
                .andExpect(view().name(RedirectConstants.REDIRECT_ADMIN_SOAM_EMUM_LIST));
    }

    @Test
    void testProcessUpdateFormSuccess() throws Exception {
        mockMvc.perform(post(URL_EDIT_SOAM_ENUM, TEST_PRIORITY_1.getId())
                        .param("name", "New Test Priority 1")
                        .param("sequence", "11"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name(RedirectConstants.REDIRECT_ADMIN_SOAM_EMUM_LIST));
    }

    @Test
    void testProcessUpdateFormError() throws Exception {
        mockMvc.perform(post(URL_EDIT_SOAM_ENUM, TEST_PRIORITY_1.getId())
                        .param("name", "Test Priority 2")
                        .param("sequence", "10"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_ADMIN_SOAM_ENUM))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_ADMIN_SOAM_ENUM, "name"))
                .andExpect(view().name(ViewConstants.VIEW_ADMIN_SOAM_EMUM_UPDATE_FORM));

        mockMvc.perform(post(URL_EDIT_SOAM_ENUM, TEST_PRIORITY_1.getId())
                        .param("name", "Test Priority 1")
                        .param("sequence", "20"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasErrors(ModelConstants.ATTR_ADMIN_SOAM_ENUM))
                .andExpect(model().attributeHasFieldErrors(ModelConstants.ATTR_ADMIN_SOAM_ENUM, "sequence"))
                .andExpect(view().name(ViewConstants.VIEW_ADMIN_SOAM_EMUM_UPDATE_FORM));
    }
}
