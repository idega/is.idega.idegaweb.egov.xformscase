package is.idega.idegaweb.egov.xforms.data;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.data.IDOHome;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/06 17:35:47 $ by $Author: anton $
 */
public interface GeneralXformCaseHome extends IDOHome{
    
    public GeneralXformCase create() throws CreateException;
    
    public GeneralXformCase findByPrimaryKey(Object pk) throws FinderException;
    

}
