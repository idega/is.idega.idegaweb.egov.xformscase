package is.idega.idegaweb.egov.xforms.data;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOFactory;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/06 17:35:47 $ by $Author: anton $
 */
public class GeneralXformCaseHomeImpl extends IDOFactory implements GeneralXformCaseHome{

	private static final long serialVersionUID = 7899756171264580719L;

	public Class getEntityInterfaceClass() {
	return GeneralXformCase.class;
    }
    
    public GeneralXformCase create() throws CreateException {
	return (GeneralXformCase) super.createIDO();
    }

    public GeneralXformCase findByPrimaryKey(Object pk) throws FinderException {
	return (GeneralXformCase) super.findByPrimaryKeyIDO(pk);
    }
    

}
