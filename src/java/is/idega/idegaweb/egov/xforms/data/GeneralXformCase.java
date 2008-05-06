package is.idega.idegaweb.egov.xforms.data;

import com.idega.block.process.data.Case;
import com.idega.data.IDOEntity;
/**
 * @author <a href="mailto:arunas@idega.com">Arūnas Vasmanas</a>
 * @version $Revision: 1.1 $
 *
 * Last modified: $Date: 2008/05/06 17:35:47 $ by $Author: anton $
 */
public interface GeneralXformCase extends IDOEntity, Case {
    
    public long getXformId();
    
    public String getXfromLocation();
    
    public void setXformId(String xformId);
    
    public void setXfromLocation(String location);
    
}
