package is.idega.idegaweb.egov.xforms.presentation;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlMessage;
import javax.faces.context.FacesContext;

import is.idega.idegaweb.egov.application.business.ApplicationType.ApplicationTypeHandlerComponent;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.application.presentation.ApplicationCreator;
import is.idega.idegaweb.egov.cases.data.CaseCategory;
import is.idega.idegaweb.egov.cases.presentation.CasesBlock;
import is.idega.idegaweb.egov.xforms.business.ApplicationTypeXForms;

import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.util.SelectorUtility;
import com.idega.webface.WFUtil;

public class UIApplicationTypeXFormsHandler extends CasesBlock implements ApplicationTypeHandlerComponent {

	private Application application;
	
	public static final String XFORMS_MENU = "xFormId";
	public static final String PARAMETER_CASE_CATEGORY = "prm_case_category";
	public static final String PARAMETER_SUB_CASE_CATEGORY = "prm_sub_case_category";
	
	private CaseCategory parentCategory;
	private CaseCategory category;
	
	@Override
	public void present(IWContext iwc) throws Exception {
//		String xFormId = iwc.getParameter(XFORMS_MENU);	
//		if (iwc.isParameterSet(PARAMETER_CASE_CATEGORY)) {
//			try {
//				parentCategory = getCasesBusiness(iwc).getCaseCategory(iwc.getParameter(PARAMETER_CASE_CATEGORY));
//			}
//			catch (FinderException fe) {
//				fe.printStackTrace();
//			}
//		}
//
//		if (iwc.isParameterSet(PARAMETER_SUB_CASE_CATEGORY)) {
//			try {
//				category = getCasesBusiness(iwc).getCaseCategory(iwc.getParameter(PARAMETER_CASE_CATEGORY));
//			}
//			catch (FinderException fe) {
//				fe.printStackTrace();
//			}
//		}
		
		DropdownMenu xFormsMenu = new DropdownMenu(XFORMS_MENU);
		xFormsMenu.setId(XFORMS_MENU);
		xFormsMenu.addMenuElement("-1", "Select");
		
		ApplicationTypeXForms appTypeXForms = getApplicationTypeXForms();
		appTypeXForms.fillMenu(xFormsMenu);
		
		SelectorUtility util = new SelectorUtility();
		DropdownMenu categories = (DropdownMenu) util.getSelectorFromIDOEntities(new DropdownMenu(PARAMETER_CASE_CATEGORY), getCasesBusiness().getCaseCategories(), "getName");
		categories.setId(PARAMETER_CASE_CATEGORY);
		categories.keepStatusOnAction(true);
		categories.setStyleClass("caseCategoryDropdown");
		categories.setMenuElementFirst("-1", "Select");
		
		Layer container = new Layer(Layer.SPAN);
		Layer errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		
		Layer layer = new Layer(Layer.DIV);
		Label label = new Label("XForm", xFormsMenu);
		HtmlMessage msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(xFormsMenu.getId());
		errorItem.add(msg);
		layer.add(label);
		layer.add(xFormsMenu);
		layer.add(errorItem);
		container.add(layer);

//		DropdownMenu subCategories = new DropdownMenu(PARAMETER_SUB_CASE_CATEGORY);
//		subCategories.setId(PARAMETER_SUB_CASE_CATEGORY);
//		subCategories.keepStatusOnAction(true);
//		subCategories.setStyleClass("subCaseCategoryDropdown");
//
//		if (parentCategory != null) {
//			Collection collection = getCasesBusiness(iwc).getSubCategories(parentCategory);
//			if (collection.isEmpty()) {
//				subCategories.addMenuElement(category.getPrimaryKey().toString(), getResourceBundle().getLocalizedString("case_creator.no_sub_category", "no sub category"));
//			}
//			else {
//				Iterator iter = collection.iterator();
//				while (iter.hasNext()) {
//					CaseCategory subCategory = (CaseCategory) iter.next();
//					subCategories.addMenuElement(subCategory.getPrimaryKey().toString(), subCategory.getLocalizedCategoryName(iwc.getCurrentLocale()));
//				}
//			}
//			subCategories.setMenuElementFirst("-1", "Select");
//		}
		
		errorItem = new Layer(Layer.SPAN);
		errorItem.setStyleClass("error");
		layer = new Layer(Layer.DIV);
		label = new Label("Case category", categories);
		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
		msg.setFor(categories.getId());
		errorItem.add(msg);
		layer.add(label);
		layer.add(categories);
		layer.add(errorItem);
		container.add(layer);
		
//		errorItem = new Layer(Layer.SPAN);
//		errorItem.setStyleClass("error");
//		layer = new Layer(Layer.DIV);
//		label = new Label("Case subcategory", subCategories);
//		msg = (HtmlMessage)iwc.getApplication().createComponent(HtmlMessage.COMPONENT_TYPE);
//		msg.setFor(subCategories.getId());
//		errorItem.add(msg);
//		layer.add(label);
//		layer.add(subCategories);
//		layer.add(errorItem);
//		container.add(layer);
		
		if(application != null) {		
			String xForm_caseCategory_Values = getApplicationTypeXForms().getSelectedElement(application);
			String[] xform_category = xForm_caseCategory_Values.split(ApplicationTypeXForms.DELIMITER);
			String xFormId = (xform_category.length >= 1) ? xform_category[0] : "-1";
			String categoryId = (xform_category.length >= 2) ? xform_category[1] : "-1";
			
			xFormsMenu.setSelectedElement(xFormId);
			categories.setSelectedElement(categoryId);
		}

		add(container);
	}
	
	public UIComponent getUIComponent(FacesContext ctx, Application app) {

		UIApplicationTypeXFormsHandler h = new UIApplicationTypeXFormsHandler();
		h.setApplication(app);
		
		return h;
	}
	
	protected ApplicationTypeXForms getApplicationTypeXForms() {
		return (ApplicationTypeXForms)WFUtil.getBeanInstance(ApplicationTypeXForms.beanIdentifier);
	}
	
	public void setApplication(Application application) {
		this.application = application;
	}

	public boolean validate(IWContext iwc) {
		boolean valid = true;
		IWResourceBundle iwrb = getResourceBundle(iwc);
		
		String xform = iwc.getParameter(XFORMS_MENU);
		String caseCategory = iwc.getParameter(PARAMETER_CASE_CATEGORY);
		String action = iwc.getParameter(ApplicationCreator.ACTION);
		
		if((xform == null || xform.equals("-1")) && ApplicationCreator.SAVE_ACTION.equals(action)) {
			iwc.addMessage(XFORMS_MENU, new FacesMessage(iwrb.getLocalizedString("xform_select", "'XForm' field value is not selected")));
			valid = false;
		}
		if((caseCategory == null || caseCategory.equals("-1")) && ApplicationCreator.SAVE_ACTION.equals(action)) {
			iwc.addMessage(PARAMETER_CASE_CATEGORY, new FacesMessage(iwrb.getLocalizedString("case_category_select", "'CaseCategory' field value is not selected")));
			valid = false;
		}
		return valid;
	}
	
	@Override
	public String getCasesProcessorType() {
		return null;
	}

	@Override
	public Map<Object, Object> getUserCasesPageMap() {
		return null;
	}

	@Override
	public boolean showCheckBox() {
		return false;
	}

	@Override
	public boolean showCheckBoxes() {
		return false;
	}
}
