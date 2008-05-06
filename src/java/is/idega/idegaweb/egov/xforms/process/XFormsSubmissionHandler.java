package is.idega.idegaweb.egov.xforms.process;

import is.idega.idegaweb.egov.cases.business.CasesBusiness;
import is.idega.idegaweb.egov.cases.business.CasesBusinessBean;
import is.idega.idegaweb.egov.cases.data.GeneralCase;
import is.idega.idegaweb.egov.cases.data.GeneralCaseHome;
import is.idega.idegaweb.egov.xforms.data.GeneralXformCase;
import is.idega.idegaweb.egov.xforms.data.GeneralXformCaseBean;
import is.idega.idegaweb.egov.xforms.data.GeneralXformCaseHome;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import org.chiba.web.IWBundleStarter;
import org.chiba.xml.dom.DOMUtil;
import org.chiba.xml.xforms.connector.AbstractConnector;
import org.chiba.xml.xforms.connector.SubmissionHandler;
import org.chiba.xml.xforms.core.Submission;
import org.chiba.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.chiba.web.xml.xforms.connector.webdav.WebdavSubmissionHandler;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.slide.business.IWSlideService;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;

/**
 * TODO: move all this logic to spring bean
 * 
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/06 17:35:49 $ by $Author: anton $
 */
public class XFormsSubmissionHandler extends AbstractConnector implements SubmissionHandler {
	
	private static final Logger logger = Logger.getLogger(XFormsSubmissionHandler.class.getName());
	private static final String form_id_tag = "form_id";
	private static final String slash = "/";
	
	public static final String SUBMITTED_DATA_PATH = "/files/forms/submissions";
    
	@SuppressWarnings("unchecked")
    public Map submit(Submission submission, Node instance) throws XFormsException {
		IWContext iwc = IWContext.getInstance();
		
		
    	//method - post, replace - none
    	if (!submission.getReplace().equalsIgnoreCase("none"))
            throw new XFormsException("Submission mode '" + submission.getReplace() + "' not supported");
    	
    	if(!submission.getMethod().equalsIgnoreCase("put") && !submission.getMethod().equalsIgnoreCase("post"))
    		throw new XFormsException("Submission method '" + submission.getMethod() + "' not supported");
    	
    	if(submission.getMethod().equalsIgnoreCase("put")) {
    		//update (put)
    		//currently unsupported
    		throw new XFormsException("Submission method '" + submission.getMethod() + "' not yet supported");	
    	} 
               
        try {
            String formId = getFormIdFromSubmissionInstance(instance);
            String location = "";
            
            if(formId != null) {
            	ByteArrayOutputStream out = new ByteArrayOutputStream();
				serialize(submission, instance, out);
				InputStream is = new ByteArrayInputStream(out.toByteArray());
				location = saveSubmittedData(formId, is);
				
				GeneralXformCase theCase = getGeneralXFormsCaseHome().create();
				theCase.setXformId(formId);
				theCase.setXfromLocation(location);
				
			
//				getCasesBusiness(iwc).storeGeneralCase(theCase, getUser(iwc), caseCategoryPK, caseTypePK, attachmentPK, message, type, caseManagerType, isPrivate, iwrb, sendMessages)
            }	            
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    	
    	return null;
    }
	
	protected String getFormIdFromSubmissionInstance(Node instance) {
		Element form_id = DOMUtil.getChildElement(instance, form_id_tag);
		
        if (form_id != null) {
        	return DOMUtil.getElementValue((Element) form_id);
        }
        return null;
	}
	
	protected String saveSubmittedData(String formId, InputStream is) throws IOException {
		
		String path = 
			new StringBuilder(SUBMITTED_DATA_PATH)
			.append(slash)
			.append(formId)
			.append(slash)
			.toString()
		;
			
		String fileName = System.currentTimeMillis() + ".xml";
		
		logger.info("Saving submitted instance to webdav path: " + path + fileName);

		IWSlideService service = getIWSlideService();
		service.uploadFileAndCreateFoldersFromStringAsRoot(path, fileName, is, "text/xml", false);
		
		return path + fileName;
	}
	
	protected IWSlideService getIWSlideService() throws IBOLookupException {
		
		try {
			return (IWSlideService) IBOLookup.getServiceInstance(IWMainApplication.getDefaultIWApplicationContext(), IWSlideService.class);
		} catch (IBOLookupException e) {
			//logger.log(Level.SEVERE, "Error getting IWSlideService");
			throw e;
		}
	}
	
	protected CasesBusiness getCasesBusiness(IWApplicationContext iwac) {
		try {
			return (CasesBusiness) IBOLookup.getServiceInstance(iwac, CasesBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
	
	private User getUser(IWContext iwc) throws RemoteException {
		try {
			return iwc.getCurrentUser();
		}
		catch (NotLoggedOnException nloe) {
			return null;
		}
	}
	
	private GeneralXformCaseHome getGeneralXFormsCaseHome() {
		try {
			return (GeneralXformCaseHome) IDOLookup.getHome(GeneralXformCase.class);
		}
		catch (IDOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
	}
}