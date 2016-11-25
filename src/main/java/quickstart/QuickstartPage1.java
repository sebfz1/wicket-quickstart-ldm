package quickstart;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * In this test, an inner LDM is supplied to an outer LDM which are both reused.<br>
 * The goal aims to double check the model is not reloaded.
 */
public class QuickstartPage1 extends WebPage
{
	private static final long serialVersionUID = 1L;
	private static final Logger log = LoggerFactory.getLogger(QuickstartPage1.class);

	private final IModel<Integer> outerModel;

	public QuickstartPage1(PageParameters parameters)
	{
		super(parameters);

		IModel<Integer> innerModel = newInnerModel();
		this.outerModel = newOuterModel(innerModel);

		this.add(new Label("label1", innerModel));
		this.add(new Label("label2", innerModel));
		this.add(new Label("label3", this.outerModel));
		this.add(new Label("label4", this.outerModel));
	}

	@Override
	public void detachModels()
	{
		super.detachModels();

		log.info("#detachModels");
		this.outerModel.detach();
	}

	static IModel<Integer> newInnerModel()
	{
		return new LoadableDetachableModel<Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Integer load()
			{
				log.info("InnerModel#load");

				return 3;
			}
		};
	}

	static IModel<Integer> newOuterModel(final IModel<Integer> innerModel)
	{
		return new LoadableDetachableModel<Integer>() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Integer load()
			{
				log.info("OuterModel#load");
				Integer i = innerModel.getObject();

				return i + 1;
			}

			@Override
			public void detach()
			{
				innerModel.detach();
			}
		};
	}
}
