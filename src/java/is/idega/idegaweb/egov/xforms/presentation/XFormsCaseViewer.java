package is.idega.idegaweb.egov.xforms.presentation;

import java.io.IOException;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.idega.block.form.presentation.FormViewer;
import com.idega.documentmanager.business.Document;
import com.idega.documentmanager.business.DocumentManagerFactory;
import com.idega.presentation.IWContext;
import com.idega.util.CoreConstants;

/**
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/06 17:35:49 $ by $Author: anton $
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
		
		super.encodeBegin(context);
		
		String formId = getXformId(context);
		
		if(formId != null)
			loadFormId(context, formId);
		

		Map<String, UIComponent> facets = (Map<String, UIComponent>)getFacets();
		
		facets.put(VIEWER_FACET, this);
//		facets.remove(VIEWER_FACET);
	}
	
	private void loadFormId(FacesContext context, String formId) {
		IWContext iwc = IWContext.getIWContext(context);
		
		DocumentManagerFactory p = new DocumentManagerFactory();;
		Document doc = p.newDocumentManager(iwc.getIWMainApplication()).takeForm(Long.parseLong(formId));
		
		HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
		session.setAttribute(XFORMS_PROPERTY, doc.getFormId());
		
		setXFormsDocument(doc.getXformsDocument());
	}
	
	@Override
	public void encodeChildren(FacesContext context) throws IOException {
		
		super.encodeChildren(context);

		@SuppressWarnings("unchecked")
		Map<String, UIComponent> facets = (Map<String, UIComponent>)getFacets();
		UIComponent viewer = facets.get(VIEWER_FACET);
		
		if(viewer != null)
			renderChild(context, viewer);
	}

	public String getFormId() {
		return formId;
	}
	
	public String getXformId(FacesContext context) {
		Map params = context.getExternalContext().getRequestMap();
		formId = getValueBinding(XFORMS_PROPERTY) != null ? (String)getValueBinding(XFORMS_PROPERTY).getValue(context) : (String)context.getExternalContext().getRequestParameterMap().get(XFORMS_PROPERTY);
		formId = CoreConstants.EMPTY.equals(formId) ? null : formId;
		setFormId(formId);
		
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}
}