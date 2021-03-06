package is.idega.idegaweb.egov.xforms.business;

import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.xforms.presentation.UIApplicationTypeXFormsHandler;
import is.idega.idegaweb.egov.xforms.presentation.XFormsCaseViewer;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.URIUtil;
import com.idega.xformsmanager.business.Form;
import com.idega.xformsmanager.business.PersistenceManager;
import com.idega.xformsmanager.business.XFormPersistenceType;

/**
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.7 $
 *
 * Last modified: $Date: 2008/11/05 09:01:31 $ by $Author: civilis $
 *
 */
@Scope("singleton")
@Service(ApplicationTypeXForms.beanIdentifier)
public class ApplicationTypeXForms implements ApplicationType {

	private PersistenceManager persistenceManager;

	public static final String beanIdentifier = "appTypeXForms";
	public static final String DELIMITER = "::";

	private static final String appType = "EGOV_XFORMS";
	private static final String egovXFormsPageType = "xforms_case_app_starter";

	@Override
	public boolean afterStore(IWContext iwc, Application app) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void beforeStore(IWContext iwc, Application app) {
		app.setElectronic(true);

		String selectedFormId = iwc.getParameter(UIApplicationTypeXFormsHandler.XFORMS_MENU);
		String caseCategoryId = iwc.getParameter(UIApplicationTypeXFormsHandler.PARAMETER_CASE_CATEGORY);

		app.setUrl(selectedFormId + DELIMITER + caseCategoryId);

	}

	@Override
	public String getBeanIdentifier() {
		return beanIdentifier;
	}

	@Override
	public ApplicationTypeHandlerComponent getHandlerComponent() {
		UIApplicationTypeXFormsHandler h = new UIApplicationTypeXFormsHandler();
		return h;
	}

	@Override
	public String getLabel(IWContext iwc) {
		return "EGOV XFORMS";
	}

	@Override
	public String getType() {
		return appType;
	}

	@Override
	public String getUrl(IWContext iwc, Application app) {

		/*
		Collection<ICPage> icpages = getPages(egovXFormsPageType);

		ICPage icPage = null;

		if(icpages == null || icpages.isEmpty()) {

//			TODO: can't create egov xforms page, as not found
			throw new RuntimeException("No egov xforms page found yet");
		}

		if(icPage == null)
			icPage = icpages.iterator().next();

		String uri = icPage.getDefaultPageURI();

		if(!uri.startsWith("/pages"))
			uri = "/pages"+uri;
		*/

		String uri = getBuilderService(iwc).getFullPageUrlByPageType(iwc, egovXFormsPageType, true);

		URIUtil uriUtil = new URIUtil(uri);
		uriUtil.setParameter(XFormsCaseViewer.XFORMS_PROPERTY, String.valueOf(app.getUrl()));
		uri = uriUtil.getUri();

//		Integer appId = getAppId(app.getPrimaryKey());

		return iwc.getIWMainApplication().getTranslatedURIWithContext(uri);
	}

	public Collection<ICPage> getPages(String pageSubType) {

		try {

			ICPageHome home = (ICPageHome) IDOLookup.getHome(ICPage.class);
			Collection<ICPage> icpages = home.findBySubType(pageSubType, false);

			return icpages;

		} catch (Exception e) {
			throw new RuntimeException("Exception while resolving icpages by subType: "+pageSubType, e);
		}
	}

	public void fillMenu(DropdownMenu menu) {

		List<Form> xforms = getPersistenceManager().getStandaloneForms();

		if(xforms != null) {
			for(Form form : xforms) {
				menu.addMenuElement(String.valueOf(form.getFormId()), form.getDisplayName());
			}
		}
	}

	public PersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	@Autowired
	@XFormPersistenceType("slide")
	public void setPersistenceManager(PersistenceManager persistenceManager) {
		this.persistenceManager = persistenceManager;
	}

	public String getSelectedElement(Application app) {
		String formId = app.getUrl();
		return formId;
	}

	@Override
	public boolean isVisible(Application app) {
		return true;
	}

//	private Integer getAppId(Object pk) {
//
//		if(pk instanceof Integer)
//			return (Integer)pk;
//		else
//			return new Integer(pk.toString());
//	}

	protected BuilderService getBuilderService(IWApplicationContext iwac) {

		try {
			return BuilderServiceFactory.getBuilderService(iwac);
		} catch (RemoteException e) {
			throw new RuntimeException("Failed to resolve builder service", e);
		}
	}
}
