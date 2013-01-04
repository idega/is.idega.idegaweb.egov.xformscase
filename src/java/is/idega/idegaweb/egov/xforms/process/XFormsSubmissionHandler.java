package is.idega.idegaweb.egov.xforms.process;

import is.idega.idegaweb.egov.cases.business.CasesBusiness;
import is.idega.idegaweb.egov.xforms.data.GeneralXformCase;
import is.idega.idegaweb.egov.xforms.data.GeneralXformCaseHome;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

import javax.jcr.RepositoryException;

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
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.repository.RepositoryService;
import com.idega.util.CoreConstants;
import com.idega.util.expression.ELUtil;

/**
 * TODO: move all this logic to spring bean
 *
 * @author <a href="mailto:anton@idega.com">Anton Makarov</a>
 * @version $Revision: 1.2 $
 *
 * Last modified: $Date: 2008/08/05 10:57:05 $ by $Author: valdas $
 */
public class XFormsSubmissionHandler extends AbstractConnector implements SubmissionHandler {

	private static final Logger logger = Logger.getLogger(XFormsSubmissionHandler.class.getName());
	private static final String form_id_tag = "form_id";
	private static final String slash = CoreConstants.SLASH;

	public static final String SUBMITTED_DATA_PATH = "/files/forms/submissions";

    @Override
	public Map submit(Submission submission, Node instance) throws XFormsException {
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
        	return DOMUtil.getElementValue(form_id);
        }
        return null;
	}

	protected String saveSubmittedData(String formId, InputStream is) throws IOException, RepositoryException {

		String path =
			new StringBuilder(SUBMITTED_DATA_PATH)
			.append(slash)
			.append(formId)
			.append(slash)
			.toString()
		;

		String fileName = System.currentTimeMillis() + ".xml";

		logger.info("Saving submitted instance to webdav path: " + path + fileName);

		RepositoryService service = ELUtil.getInstance().getBean(RepositoryService.BEAN_NAME);
		service.uploadFileAndCreateFoldersFromStringAsRoot(path, fileName, is, "text/xml");

		return path + fileName;
	}

	protected CasesBusiness getCasesBusiness(IWApplicationContext iwac) {
		try {
			return (CasesBusiness) IBOLookup.getServiceInstance(iwac, CasesBusiness.class);
		}
		catch (IBOLookupException ile) {
			throw new IBORuntimeException(ile);
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