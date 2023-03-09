package com.soam.web;

public final class RedirectConstants {
    public static final String REDIRECT_TREE_SPECIFICATION_EDIT = "redirect:/specification2/%s/edit";
    public static final String REDIRECT_TREE_STAKEHOLDER_EDIT = "redirect:/specification/%s/stakeholder2/%s/edit";
    public static final String REDIRECT_TREE_SPECIFICATION_OBJECTIVE_EDIT = "redirect:/specification/%s/specificationObjective2/%s/edit";
    public static final String REDIRECT_TREE_STAKEHOLDER_OBJECTIVE_EDIT = "redirect:/specification/%s/stakeholder/%s/stakeholderObjective2/%s/edit";
    public static final String REDIRECT_TREE_DEFAULT = "redirect:/specifications2/default";

    public static final String REDIRECT_FIND_SPECIFICATION = "redirect:/specification/find";
    public static final String REDIRECT_SPECIFICATION_LIST = "redirect:/specification/list";
    public static final String REDIRECT_SPECIFICATION_DETAILS = "redirect:/specification/%s";

    public static final String REDIRECT_SPECIFICATION_TEMPLATE_LIST = "redirect:/specification/template/list";
    public static final String REDIRECT_SPECIFICATION_TEMPLATE_EDIT = "redirect:/specification/template/%s/edit";

    public static final String REDIRECT_STAKEHOLDER_DETAILS = "redirect:/specification/%s/stakeholder/%s";

    public static final String REDIRECT_STAKEHOLDER_TEMPLATE_LIST = "redirect:/stakeholder/template/list";
    public static final String REDIRECT_STAKEHOLDER_TEMPLATE_EDIT = "redirect:/stakeholder/template/%s/edit";

    public static final String REDIRECT_SPECIFICATION_OBJECTIVE_LIST = "redirect:/specification/%s/specificationObjective/list";
    public static final String REDIRECT_SPECIFICATION_OBJECTIVE_DETAILS = "redirect:/specification/%s/specificationObjective/%s";

    public static final String REDIRECT_OBJECTIVE_TEMPLATE_LIST = "redirect:/objective/template/list";
    public static final String REDIRECT_OBJECTIVE_TEMPLATE_EDIT = "redirect:/objective/template/%s/edit";

    public static final String REDIRECT_STAKEHOLDER_OBJECTIVE_DETAILS = "redirect:/specification/%s/stakeholder/%s/stakeholderObjective/%s";

    public static final String REDIRECT_TEMPLATE_LINK_LIST = "redirect:/templateLink/list";

    public static final String REDIRECT_ADMIN_DELETE_SPECIFICATION = "redirect:/admin/deleteSpecification/list";

    public static final String REDIRECT_ADMIN_SOAM_EMUM_LIST = "redirect:/admin/soamEnum/list";

    private RedirectConstants() {
    }
}
