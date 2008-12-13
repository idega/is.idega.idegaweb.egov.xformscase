package is.idega.idegaweb.egov.xforms.presentation;

import is.idega.idegaweb.egov.xforms.business.ApplicationTypeXForms;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.idega.block.form.presentation.FormViewer;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.5 $
 *
 * Last modified: $Date: 2008/12/13 15:42:03 $ by $Author: civilis $
 */
public class XFormsCaseViewer extends FormViewer {
	
	public static final String COMPONENT_TYPE = "XFormsCaseViewer";
	
	public static final String XFORMS_PROPERTY = "xformsId";
	
	private static final String VIEWER_FACET = "viewer";
	
	private String formId;
   
	public XFormsCaseViewer() {
		super();
		setRendererType(null);
	}
	
	@Override
	protected void initializeComponent(FacesContext context) {
		super.initializeComponent(context);
		initializeXForms(context);
	}
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		

//		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
//		String formAndCategory = req.getParameter(XFORMS_PROPERTY);
//		String[] values = formAndCategory.split(ApplicationTypeXForms.DELIMITER);
//		
//		String formId = values[0];
		
		formId = getXformId(context);
		super.encodeBegin(context);
		
		
		
//		if(formId != null)
//			loadFormId(context, formId);
		

//		Map<String, UIComponent> facets = (Map<String, UIComponent>)getFacets();
		
//		facets.put(VIEWER_FACET, this);
//		facets.remove(VIEWER_FACET);
	}
	
//	private void loadFormId(FacesContext context, String formId) {
//		IWContext iwc = IWContext.getIWContext(context);
//		
//		DocumentManagerFactory p = new DocumentManagerFactory();;
//		Document doc = p.newDocumentManager(iwc.getIWMainApplication()).takeForm(Long.parseLong(formId));
//		
//		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
//		session.setAttribute(XFORMS_PROPERTY, doc.getFormId());
//		
//		setXFormsDocument(doc.getXformsDocument());
//	}
	
	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		
		super.encodeChildren(context);

		Map<String, UIComponent> facets = (Map<String, UIComponent>)getFacets();
		UIComponent viewer = facets.get(VIEWER_FACET);
		
		if(viewer != null)
			renderChild(context, viewer);
	}

	public String getFormId() {
		return formId;
	}
	
	public String getXformId(FacesContext context) {
		//super.encodeBegin(context);
		HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
		String formAndCategory = req.getParameter(XFORMS_PROPERTY);
		String[] values = formAndCategory.split(ApplicationTypeXForms.DELIMITER);
		
		String formId = values[0];
		
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
}