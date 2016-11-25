package quickstart;

import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.TextRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this test, the LDM is supplied to an ajax behavior which initiate a new Request Cycle causing the LDM to be reloaded.
 */
public class QuickstartPage2 extends WebPage
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(QuickstartPage2.class);

	private final IModel<Integer> outerModel;
	private final MyAjaxBehavior behavior;

	public QuickstartPage2(PageParameters parameters)
	{
		super(parameters);

		this.outerModel = newOuterModel();

		this.behavior = new MyAjaxBehavior(this.outerModel);
		this.add(this.behavior);

		this.add(new Label("label", this.outerModel));
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);

		response.render(new OnDomReadyHeaderItem(String.format("$.get('%s')", this.behavior.getCallbackUrl())));
	}

	@Override
	public void detachModels()
	{
		super.detachModels();

		log.info("#detachModels");
		this.outerModel.detach();
	}

	static IModel<Integer> newOuterModel()
	{
		return new LoadableDetachableModel<Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Integer load()
			{
				log.info("OuterModel#load");

				return 4;
			}
		};
	}

	static class MyAjaxBehavior extends AbstractAjaxBehavior
	{
		private static final long serialVersionUID = 1L;

		private final IModel<Integer> model;

		public MyAjaxBehavior(IModel<Integer> model)
		{
			this.model = model;
		}

		@Override
		public void onRequest()
		{
			log.info("#onRequest"); // yes, it's a new request!
			String value = String.valueOf(this.model.getObject());

			RequestCycle requestCycle = RequestCycle.get();
			requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler(value));
		}

		@Override
		public boolean rendersPage()
		{
			return false;
		}
	}
}
