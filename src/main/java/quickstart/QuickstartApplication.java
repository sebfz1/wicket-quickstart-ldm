package quickstart;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 *
 */
public class QuickstartApplication extends WebApplication
{
	@Override
	public Class<? extends Page> getHomePage()
	{
		return QuickstartPage2.class;
	}
}
