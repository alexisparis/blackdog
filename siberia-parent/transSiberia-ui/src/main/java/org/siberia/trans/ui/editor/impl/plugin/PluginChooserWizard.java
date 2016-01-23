/* 
 * TransSiberia-ui : siberia plugin frontend for TransSiberia
 *
 * Copyright (C) 2008 Alexis PARIS
 * Project Lead:  Alexis Paris
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library;
 * if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
package org.siberia.trans.ui.editor.impl.plugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import org.apache.log4j.Logger;
import org.awl.DefaultWizardController;
import org.awl.DefaultWizardPageDescriptor;
import org.awl.NavigationAuthorization;
import org.awl.DefaultWizard;
import org.awl.Wizard;
import org.awl.WizardConstants;
import org.awl.WizardPageDescriptor;
import org.awl.event.WizardModelAdapter;
import org.awl.exception.PageDescriptorChangingException;
import org.siberia.ResourceLoader;
import org.siberia.TransSiberiaUiPlugin;
import org.siberia.exception.ResourceException;
import org.siberia.trans.PluginGraph;
import org.siberia.trans.TransSiberia;
import org.siberia.trans.exception.DownloadAbortedException;
import org.siberia.trans.handler.DefaultDownloadHandler;
import org.siberia.trans.type.RepositoryUtilities;
import org.siberia.trans.type.plugin.Plugin;
import org.siberia.trans.type.plugin.PluginBuild;
import org.siberia.trans.type.plugin.Version;
import org.siberia.type.SibList;
import org.siberia.utilities.swing.TaskStatusProgressBar;
import org.siberia.utilities.task.TaskStatus;
import org.siberia.trans.download.DownloadTransaction;
import org.jdesktop.swingx.JXErrorPane;
import org.siberia.ui.swing.error.ErrorEvent;
import org.siberia.trans.download.TransactionFactory;

/**
 *
 * @author alexis
 */
public class PluginChooserWizard extends DefaultWizard
{
    /** logger */
    private Logger                     logger        =Logger.getLogger(PluginChooserWizard.class);
    
    /** choose plugin page */
    private ChoosePluginPageDescriptor choosePage    = null;
    
    /** download plugin page */
    private DownloadPageDescriptor     downloadPage  = null;
    
    /** TransSiberia */
    private TransSiberia               transSiberian = null;
    
    /** resource bundle */
    private ResourceBundle             rb            = null;
    
    /** build to download */
    private List<PluginBuild>          builds        = null;
    
    /** transaction factory */
    private TransactionFactory         transFactory  = null;
    
    /** Creates a new instance of PluginChooserWizard
     *	@param transSiberian an instance of TransSiberia
     *	@param frame a Frame
     */
    public PluginChooserWizard(TransSiberia transSiberian, Frame frame)
    {
	this(transSiberian, frame, null, null);
    }
    
    /** Creates a new instance of PluginChooserWizard
     *	@param transSiberian an instance of TransSiberia
     *	@param frame a Frame
     *	@param builds the build to install or null to choose the build to instal
     */
    public PluginChooserWizard(TransSiberia transSiberian, Frame frame, List<PluginBuild> builds, TransactionFactory factory)
    {
	super(frame);
	
	this.builds = builds;
	
	this.transFactory = factory;
	
	if ( logger.isDebugEnabled() )
	{
	    logger.debug("fixed builds to install " + (this.builds == null ? 0 : this.builds.size()));
	}
	
	org.siberia.ui.awl.WizardPreparator.prepareWizard(this);
	
	if ( builds == null )
	{
	    this.setMinimumSize(new Dimension(825, 600));
	    this.setPreferredSize(new Dimension(825, 600));
	}
	
	this.transSiberian = transSiberian;
	
	if ( builds == null )
	{
	    this.choosePage = new ChoosePluginPageDescriptor(this.transSiberian);
	    this.choosePage.setPreviousDescriptorId(WizardConstants.STARTING_DESCRIPTOR_ID);
	    this.choosePage.setNextDescriptorId("2");
	}
	
	
	this.downloadPage = new DownloadPageDescriptor(this.transSiberian);
	if ( builds == null )
	{
	    this.downloadPage.setPreviousDescriptorId("1");
	}
	else
	{
	    this.downloadPage.setPreviousDescriptorId(WizardConstants.STARTING_DESCRIPTOR_ID);
	}
        this.downloadPage.setNextDescriptorId(WizardConstants.TERMINAL_DESCRIPTOR_ID);
        
	if ( builds == null )
	{
	    this.registerWizardPanel("1", this.choosePage);
	}
        this.registerWizardPanel("2", this.downloadPage);
	
	this.rb = ResourceBundle.getBundle(PluginChooserWizard.class.getName());
	
	if ( this.choosePage != null )
	{
	    this.choosePage.setDescription(rb.getString("page.selection.description"));
	}
	this.downloadPage.setDescription(rb.getString("page.download.description"));
	
	this.setTitle(rb.getString("dialog.title"));
        
        /** add a WizardModelListener to be sure that when we try to go to
         *  second stage, a selection is made in the list
         */
        this.getModel().addWizardModelListener(new WizardModelAdapter()
        {
            /** method that is called to verify that all WizardModelListener agree to change the current descriptor
             *  @param currentDescriptor the current descriptor
             *  @param candidate the descriptor we try to set as current descriptor
             *
             *  @exception PageDescriptorChangingException if the listener refused the change
             */
            public void checkCurrentDescriptorChanging(WizardPageDescriptor currentDescriptor, WizardPageDescriptor candidate)
                    throws PageDescriptorChangingException
            {   
		if ( currentDescriptor == choosePage )
                {   
		    if ( ((PluginChooserPanel)choosePage.getComponent()).getSelectedPluginList().size() == 0 )
		    {
			throw new PageDescriptorChangingException(this, rb.getString("insufficientPluginToDownload"));
		    }
                }
            }
        });
	
	this.setController(new DefaultWizardController()
	{
	    /**
	     * method called when cancel is pressed
	     */
	    @Override
	    public void cancelButtonPressed()
	    {
		super.cancelButtonPressed();
		
		if ( downloadPage != null )
		{
		    DownloadTransaction transaction = downloadPage.getDownloadTransaction();
		    
		    if ( transaction != null )
		    {
			if ( ! transaction.isFinished() )
			{
			    transaction.setRollbackOnly();
			}
		    }
		}
	    }
	    
    
	    /** method called when the wizard is about to be hidden */
	    @Override
	    public void aboutToHideWizard()
	    {   
		super.aboutToHideWizard();
		
		if ( this.getReturnCode() == WizardConstants.WIZARD_CLOSED_OPTION )
		{
		    if ( downloadPage != null )
		    {
			DownloadTransaction transaction = downloadPage.getDownloadTransaction();

			if ( transaction != null )
			{
			    if ( ! transaction.isFinished() )
			    {
				transaction.setRollbackOnly();
			    }
			}
		    }
		}
	    }
	});
    }
    
    /** return the DownloadTransaction linked to this wizard
     *	@return a DownloadTransaction
     */
    public DownloadTransaction getDownloadTransaction()
    {
	DownloadTransaction transaction = null;
	
	WizardPageDescriptor desc = this.getModel().getDescriptor("2");
	if ( desc instanceof DownloadPageDescriptor )
	{
	    transaction = ((DownloadPageDescriptor)desc).getDownloadTransaction();
	}
	
	return transaction;
    }
    
    /** page that allow to choose the plugin to install */
    private class ChoosePluginPageDescriptor extends DefaultWizardPageDescriptor
    {
	public ChoosePluginPageDescriptor(TransSiberia transSiberian)
	{
	    super("plugin selection");
	    
	    this.setComponent(new PluginChooserPanel(transSiberian));
	}
    }
    
    /** page that allow to download the plugin to install */
    private class DownloadPageDescriptor extends DefaultWizardPageDescriptor
    {
	/** debug selected plugin */
	private boolean             debugSelectedPlugins = false;
	
	/** transSiberia */
	private TransSiberia        transSiberia         = null;
	
	/* download transaction */
	private DownloadTransaction transaction          = null;
	
	public DownloadPageDescriptor(TransSiberia transSiberian)
	{
	    super("Download plugins");
	    
	    this.setComponent(new PluginDownloadPanel());
	    
	    this.setFinishAuthorization(NavigationAuthorization.FORBIDDEN);
	    
	    this.transSiberia = transSiberian;
	    
	    this.getDownloadPanel().getStopButton().addActionListener(new ActionListener()
	    {
		public void actionPerformed(ActionEvent e)
		{
		    if ( transaction != null )
		    {
			transaction.setRollbackOnly();
			setDownloadCanceled();
		    }
		}
	    });
	}
	
	/** refresh page to show that the download is canceled */
	public void setDownloadCanceled()
	{
	    this.setFinishAuthorization(NavigationAuthorization.FORBIDDEN);
	    Runnable run = new Runnable()
	    {
		public void run()
		{
		    getDownloadPanel().getStopButton().setEnabled(false);

		    setFinishAuthorization(NavigationAuthorization.FORBIDDEN);

		    getDownloadPanel().getBuildInfoLabel().setText("");
		    getDownloadPanel().getCurrentTaskProgressBar().setValue(0);
		    getDownloadPanel().getGeneralProgressBar().setValue(0);
		    getDownloadPanel().setBuildDownloadedCount(0);

		    getDownloadPanel().getInformationLabel().setText(rb.getString("downloadCanceledLabel"));

		    try
		    {
			getDownloadPanel().getInformationLabel().setIcon(ResourceLoader.getInstance().getIconNamed(
				TransSiberiaUiPlugin.PLUGIN_ID + ";1::img/downloadCanceled.png"));
		    }
		    catch (ResourceException ex)
		    {
			getDownloadPanel().getInformationLabel().setIcon(null);
		    }
		}
	    };
	    
	    if ( SwingUtilities.isEventDispatchThread() )
	    {
		run.run();
	    }
	    else
	    {
		SwingUtilities.invokeLater(run);
	    }
	}
	
	/** refresh page to show that the download is successfull */
	public void setDownloadFinished()
	{
	    this.setFinishAuthorization(NavigationAuthorization.ALLOWED);
	    
	    Runnable run = new Runnable()
	    {
		public void run()
		{
		    getDownloadPanel().getStopButton().setEnabled(false);

		    setFinishAuthorization(NavigationAuthorization.DEFAULT);

		    getDownloadPanel().getInformationLabel().setText(rb.getString("downloadFinishedLabel"));

		    try
		    {
			getDownloadPanel().getInformationLabel().setIcon(ResourceLoader.getInstance().getIconNamed(
				TransSiberiaUiPlugin.PLUGIN_ID + ";1::img/downloadFinished.png"));
		    }
		    catch (ResourceException ex)
		    {
			getDownloadPanel().getInformationLabel().setIcon(null);
		    }
		}
	    };
	    
	    if ( SwingUtilities.isEventDispatchThread() )
	    {
		run.run();
	    }
	    else
	    {
		SwingUtilities.invokeLater(run);
	    }
	}
	
	/** return the DownloadTransaction
	 *  @return a DownloadTransaction
	 */
	public DownloadTransaction getDownloadTransaction()
	{
	    return this.transaction;
	}

	public PluginDownloadPanel getDownloadPanel()
	{
	    Component c = super.getComponent();
	    if ( c instanceof PluginDownloadPanel )
	    {
		return (PluginDownloadPanel)c;
	    }
	    else
	    {
		return null;
	    }
	}

	/**
	 * Override this method to perform functionality when the panel itself is displayed.
	 * 
	 * @param wizard the Wizard that contains this descriptor
	 */
	@Override
	public void displayingPanel(final Wizard wizard)
	{
	    this.getDownloadPanel().reinit();
	    
	    super.displayingPanel(wizard);
	    
	    PluginBuild[] _builds = null;
	    
	    if ( builds != null )
	    {
		_builds = (PluginBuild[])builds.toArray(new PluginBuild[builds.size()]);
	    }
	    else
	    {
		SibList list = ((PluginChooserPanel)choosePage.getComponent()).getSelectedPluginList();
	    
		_builds = new PluginBuild[list.size()];

		for(int i = 0; i < _builds.length; i++)
		{
		    Object current = list.get(i);
		    PluginBuild build = null;

		    if ( current instanceof Plugin )
		    {
			build = ((Plugin)current).createBuild();
		    }

		    _builds[i] = build;
		}
	    }
	    
	    final PluginBuild[] builds = _builds;
	    
	    if ( logger.isDebugEnabled() )
	    {
		logger.debug("number of plugins to download : " + (builds == null ? 0 : builds.length));
	    }
	    
	    if ( debugSelectedPlugins )
	    {
		JTextPane pane = new JTextPane();
		
		this.setComponent(pane);
		
		for(int i = 0; i < builds.length; i++)
		{
		    try
		    {
			pane.getDocument().insertString(pane.getDocument().getLength(), builds[i].getName() + " " +
							    builds[i].getPluginId() + " " +
							    builds[i].getVersion() + "\n", null);
		    }
		    catch (BadLocationException ex)
		    {	ex.printStackTrace(); }
		}
	    }
	    else
	    {
		try
		{
		    PluginGraph graph = new PluginGraph(this.transSiberia);

		    if ( transFactory == null )
		    {
			this.transaction = new DownloadTransaction(graph);
		    }
		    else
		    {
			this.transaction = transFactory.createTransaction(graph);
		    }
		    
		    ExecutorService service = Executors.newSingleThreadExecutor();
		    
		    service.submit(new Callable()
		    {
			public Object call() throws Exception
			{
			    try
			    {
				/* ask TransSiberia to begin download */
				transSiberia.downloadBuilds(transaction, new DefaultDownloadHandler()
				{
				    /** frame asking user to accept license */
				    private LicenseConfirmationDialog     licenseDialog = null;

				    /** plugin registration dialog */
				    private PluginGraphRegistrationDialog dialog        = null;

				    /** indicate the beginning of plugins graph registration */
				    @Override
				    public void pluginRegistrationBeginned()
				    {   
					this.dialog = new PluginGraphRegistrationDialog(wizard, false);
					this.dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

					/* display at center of the wizard */
					Dimension wizardDimension = PluginChooserWizard.this.getSize();
					Point     wizardPosition  = PluginChooserWizard.this.getLocationOnScreen();

					this.dialog.setLocation( wizardPosition.x + (int)((wizardDimension.width - this.dialog.getPreferredSize().width) / 2),
								 wizardPosition.y + (int)((wizardDimension.height - this.dialog.getPreferredSize().height) / 2));


					this.dialog.setVisible(true);
				    }

				    /** indicate the plugins graph registration has ended */
				    @Override
				    public void pluginRegistrationEnded()
				    {   
					this.dialog.setVisible(false);
				    }

				    /** set the global TaskStatus representing the download task
				     *	@param status a TaskStatus
				     */
				    @Override
				    public void setGlobalTaskStatus(TaskStatus status)
				    {
					super.setGlobalTaskStatus(status);

					TaskStatusProgressBar.applyStatus(status, getDownloadPanel().getGeneralProgressBar());
				    }

				    /** set the number of build to download
				     *	@param count the number of build to download
				     */
				    public void setNumberOfBuildsToDownload(int count)
				    {
					getDownloadPanel().setBuildToDownloadCount(count);
				    }

				    /**
				     * ask for license acceptation
				     * 
				     * @param transSiberia the TransSiberia currently being used
				     * @param build the build that ask for license acceptation
				     * @return true if the license is accepted, false else
				     */
				    @Override
				    public boolean confirmLicense(TransSiberia transSiberia, PluginBuild build)
				    {
					boolean result = false;

					if ( build != null )
					{
					    String license = build.getLicenseName();
					    if ( license == null || license.trim().length() == 0 )
					    {   result = true; }
					    else
					    {
						Map<String, String> licenseContents = RepositoryUtilities.getLicensesFor(build.getRepository());

						if ( licenseContents != null )
						{
						    final String licenseContent = licenseContents.get(license);

						    if ( licenseContent != null )
						    {
							/** synchronization object */
							final Object synchronizer = new Object();

	    //					    if ( ! SwingUtilities.isEventDispatchThread() )
	    //					    {
	    //						try
	    //						{
	    //						    SwingUtilities.invokeAndWait(new Runnable()
	    //						    {
	    //							public void run()
	    //							{
	    //							    try
	    //							    {
	    //								synchronizer.wait();
	    //							    }
	    //							    catch(InterruptedException e)
	    //							    {   e.printStackTrace(); }
	    //							}
	    //						    });
	    //						}
	    //						catch(Exception e)
	    //						{   e.printStackTrace(); }
	    //					    }

							Frame frame = null;
							if ( getOwner() instanceof Frame )
							{
							    frame = (Frame)getOwner();
							}

							if ( this.licenseDialog == null )
							{
							    this.licenseDialog = new LicenseConfirmationDialog(PluginChooserWizard.this, true);
							}

							final String name = build.getName();
							final String version = (build.getVersion().equals(Version.UNKNOWN_VERSION) ? null : build.getVersion().toString());

							Runnable runnable = new Runnable()
							{
							    public void run()
							    {
								licenseDialog.setPluginContext(name, version);
								licenseDialog.setLicenseContent(licenseContent);

								Dimension wizardDimension = PluginChooserWizard.this.getSize();
								Point     wizardPosition  = PluginChooserWizard.this.getLocationOnScreen();

								licenseDialog.setLocation( wizardPosition.x + (int)((wizardDimension.width - licenseDialog.getPreferredSize().width) / 2),
											   wizardPosition.y + (int)((wizardDimension.height - licenseDialog.getPreferredSize().height) / 2));

								licenseDialog.setVisible(true);

	    //						    synchronizer.notifyAll();
							    }
							};

	    //					    if ( SwingUtilities.isEventDispatchThread() )
							if ( true )
							{
							    runnable.run();

							    result = licenseDialog.isLicenseAccepted();
							}
							else
							{
							    SwingUtilities.invokeLater(runnable);

							    try
							    {
								synchronizer.wait();

								result = licenseDialog.isLicenseAccepted();

								synchronizer.notifyAll();
							    }
							    catch(InterruptedException e)
							    {
								e.printStackTrace();
							    }
							}
						    }
						}
					    }
					}

					return result;
				    }

				    /**
				     * indicate that the download of the given build is finished
				     * 
				     * @param build the build taht is currently being downloaded
				     * @param partialStatus a TaskStatus
				     * @param hasNext true if there are other Build to download
				     */
				    @Override
				    public void buildDownloadFinished(PluginBuild build, TaskStatus partialStatus, boolean hasNext)
				    {
					super.buildDownloadFinished(build, partialStatus, hasNext);

					getDownloadPanel().getBuildInfoLabel().setText("");
					if ( hasNext )
					{
					    getDownloadPanel().getCurrentTaskProgressBar().setValue(0);
					}

					getDownloadPanel().incrementBuildDownloadedCount();
				    }

				    /**
				     * indicate that the download of the given build has begun
				     * 
				     * @param build the build taht is currently being downloaded
				     * @param partialStatus a TaskStatus
				     */
				    public void buildDownloadBegan(PluginBuild build, TaskStatus partialStatus)
				    {
					super.buildDownloadBegan(build, partialStatus);

					StringBuffer buffer = new StringBuffer();

					if ( build.getName() != null )
					{
					    buffer.append(build.getName() + " ");
					}
					if ( build.getVersion() != null && ! Version.UNKNOWN_VERSION.equals(build.getVersion()) )
					{
					    buffer.append("(" + build.getVersion() + ")");
					}

					getDownloadPanel().getBuildInfoLabel().setText(buffer.toString());
					TaskStatusProgressBar.applyStatus(partialStatus, getDownloadPanel().getCurrentTaskProgressBar());
				    }

				    /** handle an error during registration phase
				     *	@param graph the PluginGraph
				     *	@param message the message of the error
				     *	@param throwable the throwable which gives the StackTrace
				     *	
				     *	@return true to try again
				     *
				     *	@exception DownloadAbortedException if the error provoke the download stop
				     */
				    public boolean  handleErrorOnRegistration(PluginGraph graph, String message, Throwable throwable) throws DownloadAbortedException
				    {
					/** display error */
					ErrorEvent evt = new ErrorEvent(transSiberia,
									"registration error",
									message,
									"TransSiberia",
									throwable,
									Level.SEVERE);

					/** display a JXErrorDialog */
					JXErrorPane.showDialog(PluginChooserWizard.this, evt.createErrorInfo());

					throw new DownloadAbortedException(message, throwable);
				    }

				    /** handle an error on download
				     *	@param graph the PluginGraph
				     *	@param message the messageof the error
				     *	@param throwable the throwable which gives the StackTrace
				     *	
				     *	@return true to try again
				     *
				     *	@exception DownloadAbortedException if the error provoke the download stop
				     */
				    public boolean handleErrorOnDownload(PluginGraph graph, String message, Throwable throwable) throws DownloadAbortedException
				    {
					/** display error */
					ErrorEvent evt = new ErrorEvent(transSiberia,
									"download error",
									message,
									"TransSiberia",
									throwable,
									Level.SEVERE);

					/** display a JXErrorDialog */
					JXErrorPane.showDialog(PluginChooserWizard.this, evt.createErrorInfo());

					throw new DownloadAbortedException(message, throwable);
				    }

				}, builds);
			    }
			    catch(Exception e)
			    {
				transaction.setRollbackOnly();
				e.printStackTrace();
			    }
			    
			    return null;
			}
		    });

		    service.submit(new Runnable()
		    {
			public void run()
			{
			    transaction.begin();

			    Runnable runnable = new Runnable()
			    {
				public void run()
				{
				    while(true)
				    {
					try
					{
					    Thread.sleep(100);
					}
					catch (InterruptedException ex)
					{
					    ex.printStackTrace();
					}

					if ( transaction.isFinished() )
					{
					    if ( transaction.isRollbackOnly() )
					    {
						setDownloadCanceled();
					    }
					    else
					    {
						setDownloadFinished();
					    }
					    break;
					}
					else
					{
					    if ( logger.isDebugEnabled() )
					    {
						logger.debug("download transaction not finished");
					    }
					}
				    }
				}
			    };

			    new Thread(runnable).start();
			}
		    });
		}
		catch(Exception e)
		{
		    e.printStackTrace();
		}
	    }
	}

    }
    
}
