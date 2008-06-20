package is.idega.idegaweb.egov.xforms.business;

import is.idega.idegaweb.egov.application.business.ApplicationType;
import is.idega.idegaweb.egov.application.business.ApplicationTypePluggedInEvent;
import is.idega.idegaweb.egov.application.data.Application;
import is.idega.idegaweb.egov.xforms.presentation.UIApplicationTypeXFormsHandler;
import is.idega.idegaweb.egov.xforms.presentation.XFormsCaseViewer;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.IDOLookup;
import com.idega.documentmanager.business.PersistedForm;
import com.idega.documentmanager.business.PersistenceManager;
import com.idega.documentmanager.business.XFormPersistenceType;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.util.URIUtil;

/**
 * Interface is meant to be extended by beans, reflecting application type for egov applications
 * 
 * @author <a href="anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.3 $
 *
 * Last modified: $Date: 2008/06/20 09:54:44 $ by $Author: civilis $
 *
 */

@Service(ApplicationTypeXForms.beanIdentifier)
@Scope("singleton")
public class ApplicationTypeXForms implements ApplicationType, ApplicationContextAware, ApplicationListener {
	
	private ApplicationContext ctx;
	private PersistenceManager persistenceManager;
	
	public static final String beanIdentifier = "appTypeXForms";
	public static final String DELIMITER = "::";
	
	private static final String appType = "EGOV_XFORMS";
	private static final String egovXFormsPageType = "xforms_case_app_starter";
	
	public boolean afterStore(IWContext iwc, Application app) {
		// TODO Auto-generated method stub
		return false;
	}

	public void beforeStore(IWContext iwc, Application app) {
		app.setElectronic(true);
		
		String selectedFormId = iwc.getParameter(UIApplicationTypeXFormsHandler.XFORMS_MENU);
		String caseCategoryId = iwc.getParameter(UIApplicationTypeXFormsHandler.PARAMETER_CASE_CATEGORY);
		
		app.setUrl(selectedFormId + DELIMITER + caseCategoryId);
		
	}

	public ApplicationTypeHandlerComponent getHandlerComponent() {		
		UIApplicationTypeXFormsHandler h = new UIApplicationTypeXFormsHandler();
		return h;
	}

	public String getLabel(IWContext iwc) {
		return "EGOV XFORMS";
	}

	public String getType() {
		return appType;
	}

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

	public void setApplicationContext(ApplicationContext applicationcontext)
			throws BeansException {
		ctx = applicationcontext;
	}

	public void onApplicationEvent(ApplicationEvent applicationevent) {
		
		if(applicationevent instanceof ContextRefreshedEvent) {
			
			ApplicationTypePluggedInEvent event = new ApplicationTypePluggedInEvent(this);
			event.setAppTypeBeanIdentifier(beanIdentifier);
			ctx.publishEvent(event);
		}
	}
	
	public Collection<ICPage> getPages(String pageSubType) {
		
		try {
		
			ICPageHome home = (ICPageHome) IDOLookup.getHome(ICPage.class);
			@SuppressWarnings("unchecked")
			Collection<ICPage> icpages = home.findBySubType(pageSubType, false);
			
			return icpages;
			
		} catch (Exception e) {
			throw new RuntimeException("Exception while resolving icpages by subType: "+pageSubType, e);
		}
	}
	
	public void fillMenu(DropdownMenu menu) {

		List<PersistedForm> xforms = getPersistenceManager().getStandaloneForms();
		
		if(xforms != null) {
			for(PersistedForm form : xforms) {
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
