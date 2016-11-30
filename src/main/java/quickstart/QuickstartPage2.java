package quickstart;

import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ChainingModel;
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

	private final IModel<Integer> model;
	private final MyAjaxBehavior behavior;

	public QuickstartPage2(PageParameters parameters)
	{
		super(parameters);

		this.model = newModel();

		this.behavior = new MyAjaxBehavior(this.model);
		this.add(this.behavior);

		this.add(new Label("label", newChainingModel(this.model)));
	}

	@Override
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);

		response.render(new OnDomReadyHeaderItem(String.format("$.get('%s')", this.behavior.getCallbackUrl())));
	}

	static IModel<Integer> newModel()
	{
		return new LoadableDetachableModel<Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Integer load()
			{
				log.info("LDM#load");

				return 4;
			}

			@Override
			protected void onDetach()
			{
				super.onDetach();

				log.info("LDM#onDetach");
			}
		};
	}

	static IModel<?> newChainingModel(IModel<Integer> model)
	{
		return new ChainingModel<Integer>(model) {

			private static final long serialVersionUID = 1L;

			@Override
			public void detach()
			{
				// cancel detach
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
			log.info("Behavior#onRequest"); // yes, it's a new request!
			String value = String.valueOf(this.model.getObject());

			RequestCycle requestCycle = RequestCycle.get();
			requestCycle.scheduleRequestHandlerAfterCurrent(new TextRequestHandler(value));

			this.model.detach();
		}

		@Override
		public boolean rendersPage()
		{
			return false;
		}
	}
}
