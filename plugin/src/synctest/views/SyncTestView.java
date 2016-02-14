package synctest.views;

import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.*;

import synctest.testing.SyncTestRunnable;
import synctest.testing.SyncTestRunner;
import synctest.util.ExecutionResult;
import synctest.util.Result;

import org.eclipse.ui.ISharedImages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.*;


public class SyncTestView extends ViewPart {

	public static final String ID = "synctest.views.SyncTestView";

	private synctest.testing.SyncTestRunner runner;

	private double pass = 0, fail = 0, error = 0, dead = 0, total = 0;

	private Vector<Result> results;
	private Vector<ExecutionResult> executionResults;
	private Result selection;

	IWorkbench 		workbench 	= PlatformUI.getWorkbench();
	ISharedImages 	images 		= workbench.getSharedImages();


	//CONFIG TAB
	CTabItem 		config;
	CTabItem		testing;
    Composite 		composite;
    GridData 		gridData;
    GridLayout 		gridLayout;

    Text 			baseDir, sourceDir, testDir, outputDir;

    Text 			sleepAmnt, testCountAmnt;
    Scale 			threshold;

    Button 			passBox, failBox, deadBox;

    //TESTING TAB
    ToolBar 		toolbar;
    ToolItem 		item, item2, item3, item4, item5, item6;

	ProgressBar 	progress, execProgress;
    Label			running, execRunning, testLabel, execLabel;
    Text 			passAmnt, failAmnt, errAmnt, deadAmnt;
    boolean			passAmntIsFraction = false, failAmntIsFraction = false,
    				errAmntIsFraction = false, deadAmntIsFraction = false;

    Canvas 			canvas;
    Canvas 			detailCanvas;

    Tree 			resultTree;

    ToolBar 		toolbar2;
    ToolItem 		item7, item8, item9;
    Text 			testDetails;

    Button 			run;

	/**
	 * The constructor.
	 */
	public SyncTestView() {}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
	    //Create tabs for configuration and testing
		CTabFolder tabs = new CTabFolder(parent, SWT.NONE);
		createConfigTab(tabs);
		createTestingTab(tabs);
	}

	private void createConfigTab(CTabFolder tabFolder) {
	    config = new CTabItem(tabFolder, SWT.NONE);
	    config.setText("Settings");
	    tabFolder.setSelection(config);

	    composite = new Composite(tabFolder, SWT.NULL);
		gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		gridLayout = new GridLayout();
		composite.setLayout(gridLayout);

		//Group for configuring folders
		Group folders = new Group(composite, SWT.NULL);
		folders.setText("Directory Settings");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		folders.setLayoutData(gridData);
		folders.setLayout(new GridLayout(2, false));

		baseDir = new Text(folders, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		baseDir.setLayoutData(gridData);
		baseDir.setMessage("Select the project base directory");

		Button setBaseDir = new Button(folders, SWT.PUSH);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		setBaseDir.setLayoutData(gridData);
		setBaseDir.setText("  ...  ");

		Button findDirs = new Button(folders, SWT.CHECK);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		findDirs.setLayoutData(gridData);
		findDirs.setText("Automatically find souce and test directories");

		setBaseDir.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        switch (e.type) {
		        case SWT.Selection:
		            DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
		            dialog.setMessage("Select the base directory for your project");

		            if(System.getProperty("os.name").equals("Linux")) {
		            	dialog.setFilterPath("/home");
		            }

		            String path = dialog.open();
		            baseDir.setText(path);
		            if(findDirs.getSelection() == true) importDirs(path);
		          break;
		        }
		      }
		    });

		Label seperator = new Label(folders, SWT.SEPARATOR | SWT.HORIZONTAL);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		seperator.setLayoutData(gridData);

		sourceDir = new Text(folders, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		sourceDir.setLayoutData(gridData);
		sourceDir.setMessage("Select the directory containing source code");

		Button setSourceDir = new Button(folders, SWT.PUSH);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		setSourceDir.setLayoutData(gridData);
		setSourceDir.setText("  ...  ");

	    setSourceDir.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        switch (e.type) {
		        case SWT.Selection:
		            DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
		            dialog.setMessage("Select the directory containing project source files");

		            if(System.getProperty("os.name").equals("Linux")) {
		            	dialog.setFilterPath("/home");
		            }
		            sourceDir.setText(dialog.open());
		          break;
		        }
		      }
		    });

		testDir = new Text(folders, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		testDir.setLayoutData(gridData);
		testDir.setMessage("Select the directory containing junit tests");

		Button setTestDir = new Button(folders, SWT.PUSH);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		setTestDir.setLayoutData(gridData);
		setTestDir.setText("  ...  ");

		setTestDir.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		        switch (e.type) {
		        case SWT.Selection:
		            DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
		            dialog.setMessage("Select the directory containing your test files");

		            if(System.getProperty("os.name").equals("Linux")) {
		            	dialog.setFilterPath("/home");
		            }
		            sourceDir.setText(dialog.open());
		          break;
		        }
		      }
		    });

//		outputDir = new Text(folders, SWT.NONE);
//		gridData = new GridData(GridData.FILL_HORIZONTAL);
//		outputDir.setLayoutData(gridData);
//		outputDir.setMessage("Select the directory for output files");
//
//		Button setOutputDir = new Button(folders, SWT.PUSH);
//		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		setOutputDir.setLayoutData(gridData);
//		setOutputDir.setText("  ...  ");
//
//		setOutputDir.addListener(SWT.Selection, new Listener() {
//		      public void handleEvent(Event e) {
//		        switch (e.type) {
//		        case SWT.Selection:
//		            DirectoryDialog dialog = new DirectoryDialog( PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.OPEN );
//		            dialog.setMessage("Select the directory to place the output files");
//
//		            if(System.getProperty("os.name").equals("Linux")) {
//		            	dialog.setFilterPath("/home");
//		            }
//		            sourceDir.setText(dialog.open());
//		          break;
//		        }
//		      }
//		    });

	    /**************************************************************************************************/

	    // Group for setting test values such as sleep amount and num of repetitions
	    Group settings = new Group(composite, SWT.NULL);
		settings.setText("Test Settings");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		settings.setLayoutData(gridData);
		settings.setLayout(new GridLayout(6, false));

		Label checkLbl = new Label( settings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER );
		gridData.horizontalSpan = 2;
		checkLbl.setLayoutData( gridData );
		checkLbl.setText("Check for deadlocks every ");

		sleepAmnt = new Text(settings, SWT.NULL);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		sleepAmnt.setLayoutData(gridData);
		sleepAmnt.setText("0.6");

		Label seconds = new Label(settings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		seconds.setLayoutData(gridData);
		seconds.setText(" seconds");

		Label testCountLbl = new Label( settings, SWT.NULL );
		gridData = new GridData( GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER );
		testCountLbl.setLayoutData( gridData );
		testCountLbl.setText("Run each test ");

		testCountAmnt = new Text(settings, SWT.NULL);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		testCountAmnt.setLayoutData(gridData);
		testCountAmnt.setText("100");

		Label times = new Label(settings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		times.setLayoutData(gridData);
		times.setText(" time(s)");

		Label thresholdLbl = new Label(settings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		thresholdLbl.setLayoutData(gridData);
		thresholdLbl.setText("Pass Threshold: ");

		threshold = new Scale(settings, SWT.HORIZONTAL);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		threshold.setLayoutData(gridData);
		threshold.setSelection(70);

		Label scaleLbl = new Label(settings, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		scaleLbl.setLayoutData(gridData);
		scaleLbl.setText(threshold.getSelection() + "%");

		threshold.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		    	  scaleLbl.setText(threshold.getSelection() + "%");
		      }
		    });


		/**************************************************************************************************/

		Group parser = new Group(composite, SWT.NULL);
		parser.setText("Parser Settings");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		parser.setLayoutData(gridData);
		parser.setLayout(new GridLayout(1, false));

		passBox = new Button(parser, SWT.CHECK);
		passBox.setText("Count Test Passes");
		passBox.setSelection(true);

		failBox = new Button(parser, SWT.CHECK);
		failBox.setText("Count Test Failures");
		failBox.setSelection(true);

		deadBox = new Button(parser, SWT.CHECK);
		deadBox.setText("Count Test Deadlocks");
		deadBox.setSelection(true);

		config.setControl(composite);
	}

	private void createTestingTab(CTabFolder tabFolder) {
		testing = new CTabItem(tabFolder, SWT.NONE);
	    testing.setText("Testing");

	    composite = new Composite(tabFolder, SWT.NULL);
		gridData = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gridData);
		gridLayout = new GridLayout(2, false);
		composite.setLayout(gridLayout);

		Group tests = new Group(composite, SWT.NULL);
		tests.setText("Testing");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		tests.setLayoutData(gridData);
		tests.setLayout(new GridLayout(4, false));

		// TODO populate with buttons similar to JUnit
		toolbar = new ToolBar(tests, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		gridData.horizontalSpan = 4;
		toolbar.setLayoutData(gridData);

		item = new ToolItem(toolbar, SWT.PUSH);
		item.setToolTipText("Button 1");
		item.setImage(getDefaultImage());
		
		item2 = new ToolItem(toolbar, SWT.CHECK);
		item2.setToolTipText("Button 2");
		item2.setImage(getDefaultImage());
		
		item3 = new ToolItem(toolbar, SWT.CHECK);
		item3.setToolTipText("Button 3");
		item3.setImage(getDefaultImage());
		
		item4 = new ToolItem(toolbar, SWT.CHECK);
		item4.setToolTipText("Button 4");
		item4.setImage(getDefaultImage());
		
		item5 = new ToolItem(toolbar, SWT.CHECK);
		item5.setToolTipText("Button 5");
		item5.setImage(getDefaultImage());
		
		item6 = new ToolItem(toolbar, SWT.CHECK);
		item6.setToolTipText("Button 6");
		item6.setImage(getDefaultImage());

		// Label to show currently running test
		running = new Label(tests, SWT.LEFT);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		gridData.horizontalSpan = 4;
		running.setLayoutData(gridData);
		running.setText("Tests Progress\t\t\t\t\t");

		// Progress bar for test progress
		progress = new ProgressBar(tests, SWT.NULL);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		progress.setMinimum(0);
		progress.setSelection(0);
		progress.setLayoutData(gridData);

		// Progress bar for execution progress
		execRunning = new Label(tests, SWT.LEFT);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING | GridData.VERTICAL_ALIGN_CENTER);
		gridData.horizontalSpan = 4;
		execRunning.setLayoutData(gridData);
		execRunning.setText("Executions Progress\t\t\t\t\t");

		// Progress bar for execution progress
		execProgress = new ProgressBar(tests, SWT.NULL);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		progress.setMinimum(0);
		execProgress.setLayoutData(gridData);
		execProgress.setMinimum(0);
		execProgress.setSelection(0);

		Label passed = new Label(tests, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		passed.setLayoutData(gridData);
		passed.setText("Passes:");

		passAmnt = new Text(tests, SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		passAmnt.setLayoutData(gridData);
		passAmnt.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
		passAmnt.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		passAmnt.setText("0.00%");

		passAmnt.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(passAmntIsFraction) {
					passAmnt.setText(String.format("%.2f", pass/total*100)+"%");
					passAmntIsFraction = false;
				} else {
					passAmnt.setText((int)pass+"/"+(int)total);
					passAmntIsFraction = true;
				}
			}

		});

		Label failed = new Label(tests, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		failed.setLayoutData(gridData);
		failed.setText("Failures:");

		failAmnt = new Text(tests, SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		failAmnt.setLayoutData(gridData);
		failAmnt.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
		failAmnt.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		failAmnt.setText("0.00%");

		failAmnt.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(failAmntIsFraction) {
					failAmnt.setText(String.format("%.2f", fail/total*100)+"%");
					failAmntIsFraction = false;
				} else {
					failAmnt.setText((int)fail+"/"+(int)total);
					failAmntIsFraction = true;
				}
			}

		});

		Label errorLbl = new Label(tests, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		errorLbl.setLayoutData(gridData);
		errorLbl.setText("Errors:");

		errAmnt = new Text(tests, SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		errAmnt.setLayoutData(gridData);
		errAmnt.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
		errAmnt.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		errAmnt.setText("0.00%");

		errAmnt.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(errAmntIsFraction) {
					errAmnt.setText(String.format("%.2f", error/total*100)+"%");
					errAmntIsFraction = false;
				} else {
					errAmnt.setText((int)error+"/"+(int)total);
					errAmntIsFraction = true;
				}
			}

		});

		Label deadlocked = new Label(tests, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		deadlocked.setLayoutData(gridData);
		deadlocked.setText("Deadlocks:");

		deadAmnt = new Text(tests, SWT.READ_ONLY);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		deadAmnt.setLayoutData(gridData);
		deadAmnt.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));
		deadAmnt.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		deadAmnt.setText("0.00%");

		deadAmnt.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				if(deadAmntIsFraction) {
					deadAmnt.setText(String.format("%.2f", dead/total*100)+"%");
					deadAmntIsFraction = false;
				} else {
					deadAmnt.setText((int)dead+"/"+(int)total);
					deadAmntIsFraction = true;
				}
			}

		});

		canvas = new Canvas(tests, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		gridData.heightHint = 20;
		canvas.setLayoutData(gridData);
		
		canvas.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				canvas.setToolTipText("Pass: "+(int)pass+", Fail: "+(int)fail+", Error: "+(int)error+", Deadlock: "+(int)dead);
				Rectangle clientArea = canvas.getClientArea();
				//pass
				e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
				e.gc.fillRectangle(clientArea.x, clientArea.y, (int)(clientArea.width*(pass/total)), clientArea.height);
				
				//fail
				e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
				e.gc.fillRectangle((int)(clientArea.width*(pass/total)), clientArea.y, (int)(clientArea.width*(fail/total)), clientArea.height);
				
				//error
				e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
				e.gc.fillRectangle((int)(clientArea.width*(pass/total))+(int)(clientArea.width*(fail/total)), 
					clientArea.y,(int)(clientArea.width*(error/total)), clientArea.height);
				
				//deadlock
				e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));
				e.gc.fillRectangle((int)(clientArea.width*(pass/total))+(int)(clientArea.width*(fail/total))+
						(int)(clientArea.width*(error/total)),  clientArea.y, 
						(int)(clientArea.width*(dead/total)), clientArea.height);
			}
		});

		detailCanvas = new Canvas(tests, SWT.NONE);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 4;
		gridData.heightHint = 20;
		detailCanvas.setLayoutData(gridData);

		Group testResults = new Group(tests, SWT.NULL);
		testResults.setText("Test Results");
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		testResults.setLayoutData(gridData);
		testResults.setLayout(new GridLayout(8, false));

		resultTree = new Tree(testResults, SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 4;
		resultTree.setLayoutData(gridData);

		final Menu menu = new Menu(resultTree);
	    resultTree.setMenu(menu);
	    menu.addMenuListener(new MenuAdapter() {
	        public void menuShown(MenuEvent e) {
	            MenuItem[] items = menu.getItems();
	            for (int i = 0; i < items.length; i++)
	            {
	                items[i].dispose();
	            }
	            MenuItem newItem = new MenuItem(menu, SWT.NONE);
	            newItem.setText("Menu for " + resultTree.getSelection()[0].getText());
	        }
	    });

		/**************************************************************************************************/

		Group details = new Group(composite, SWT.NULL);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;
		details.setLayoutData(gridData);
		details.setLayout(new GridLayout(1, false));
		details.setText("Test Details");

		toolbar2 = new ToolBar(details, SWT.NULL);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		toolbar2.setLayoutData(gridData);

		item7 = new ToolItem(toolbar2, SWT.CHECK);
		item7.setToolTipText("Button 7");
		item7.setImage(getDefaultImage());
		item8 = new ToolItem(toolbar2, SWT.CHECK);
		item8.setToolTipText("Button 8");
		item8.setImage(getDefaultImage());
		item9 = new ToolItem(toolbar2, SWT.CHECK);
		item9.setToolTipText("Button 9");
		item9.setImage(getDefaultImage());

		testDetails = new Text(details, SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		testDetails.setLayoutData(gridData);

		resultTree.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
					if (event.item instanceof TreeItem) {
		    			for(int i = 0; i < results.size(); i++) {
		    				if(results.get(i).getName().equals(((TreeItem)event.item).getText())) {
		    					selection = results.get(i);
		    					break;
		    				}
		    			}
		    			
		    			testDetails.setText(selection.getRaw());
		    			
		    			detailCanvas.redraw();
		    			detailCanvas.addPaintListener(new PaintListener() {
		    				public void paintControl(PaintEvent e) {
		    					detailCanvas.setToolTipText(selection.getName()+" - "+"Pass: "+(int)selection.getPass()+
		    							", Fail: "+(int)selection.getFail()+", Error: "+(int)selection.getError()+
		    							", Deadlock: "+(int)selection.getDeadlock());
		    		            
		    					Rectangle clientArea = detailCanvas.getClientArea();
		    		            
		    		            //pass
								e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
								e.gc.fillRectangle(clientArea.x, clientArea.y, 
										(int)(clientArea.width*(selection.getPass()/selection.getTotal())), clientArea.height);
								
								//fail
								e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
								e.gc.fillRectangle((int)(clientArea.width*(selection.getPass()/selection.getTotal())), 
										clientArea.y, (int)(clientArea.width*(selection.getFail()/selection.getTotal())), clientArea.height);
								
								//error
								e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW));
								e.gc.fillRectangle((int)(clientArea.width*(selection.getPass()/selection.getTotal())) + 
										(int)(clientArea.width*(selection.getFail()/selection.getTotal())), 
										clientArea.y,(int)(clientArea.width*(selection.getError()/selection.getTotal())), clientArea.height);
								
								//deadlock
								e.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_BLUE));
								e.gc.fillRectangle((int)(clientArea.width*(selection.getPass()/selection.getTotal())) + 
										(int)(clientArea.width*(selection.getFail()/selection.getTotal())) + 
										(int)(clientArea.width*(selection.getError()/selection.getTotal())), 
										clientArea.y, (int)(clientArea.width*(selection.getDeadlock()/selection.getTotal())), clientArea.height);
		    				}
		    	    	});
		    		}
		      	}
		    });

		/**************************************************************************************************/

    	run = new Button(composite, SWT.PUSH);
		run.setText("Run Tests");
		run.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event event) {
		        switch (event.type) {
		        case SWT.Selection:
		    		clearResultWidgets(); // if we're running again
		        	clearOutputFile();
		        	runTests();
		        }
		      }
		    });

		Button cancel = new Button(composite, SWT.PUSH);
		cancel.setText("Cancel");

		testing.setControl(composite);
	}

	public void runTests() {
		File file = new File(testDir.getText());
    	
		progress.setMaximum(file.listFiles(new FilenameFilter() {
			public boolean accept(File f, String s) {
				if(s.contains("java")) return true;
				return false;
			}
    	}).length); //lengthy way to get number of tests in directory
    	
    	execProgress.setMaximum(Integer.valueOf(testCountAmnt.getText()));

    	runner = new SyncTestRunner(baseDir.getText(), sourceDir.getText(),
    			testDir.getText(), sleepAmnt.getText(), testCountAmnt.getText());

    	// Start the thread for running tests
    	(new Thread(new SyncTestRunnable(runner))).start();
    	
    	// Start the thread for getting test results
    	(new Thread() {
    		public void run() {
    			try {
    				BufferedReader in = new BufferedReader(new FileReader("syncTestOutput.txt"));
    				String line;
    				while(true) {
    					if((line = in.readLine()) != null) {
    						//System.out.println("Reading: " + line);
    						// grab the output line from the other thread and pass it on to update UI

    						if(line.contains("finished")) {
    							in.close();
    							updateResultWidgets(line);
    							break;
    						}
    						
    						updateResultWidgets(line);
    					} else {
    						//System.out.println("Waiting to read...");
    						Thread.sleep(500); // poll the file every x seconds
    					}
    				}
    	
    				while(runner.getResults() == null) {
    					; // wait
    				}
    				
    				populateResultWidgets();
    			} catch(Exception e) {
    				e.printStackTrace();
    			}
    		}
    	}).start();
	}

	public void importDirs(String path) {
		File file = new File(path);
		String[] names = file.list();

		for(String name : names) {
			if(new File(path + "/" + name).isDirectory()) {
				if(name.contains("source") || name.contains("src")) {
					sourceDir.setText(path + "/" + name);
				} else if(name.contains("test") || name.contains("tst")) {
					testDir.setText(path + "/" + name);
				}
			}
		}
	}

	public void populateResultWidgets() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				results = runner.getResults();

				//iterate through results, populate tree
				for(int i = 0; i < results.size(); i++) {

					TreeItem test = new TreeItem(resultTree, SWT.NONE);
					test.setText(results.get(i).getName());

					TreeItem testPass = new TreeItem(test, SWT.NONE);
					testPass.setText("Pass: "+ (int)results.get(i).getPass());

					TreeItem testFail = new TreeItem(test, SWT.NONE);
					testFail.setText("Fail: "+ (int)results.get(i).getFail());

					TreeItem testError = new TreeItem(test, SWT.NONE);
					testError.setText("Error: "+ (int)results.get(i).getError());

					TreeItem testDeadlock = new TreeItem(test, SWT.NONE);
					testDeadlock.setText("Deadlock: "+ (int)results.get(i).getDeadlock());
				
				}
			}
		});
	}

	public void updateResultWidgets(String line) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				
				//update progress bars
				if(line.contains("Running")) {
					// A new set of tests is running, reset executions progress
					running.setText(line);
					execProgress.setSelection(0);
					
				} else if(line.contains("Completed")) {
					// A set of tests has finished, update first progress bar
					progress.setSelection(progress.getSelection()+1);
					execRunning.setText("Execution 0/"+testCountAmnt.getText());
					
				} else if(line.contains("Execution")) {
					// An execution has finished, update execution progress bar/label	
					execProgress.setSelection(execProgress.getSelection()+1);
					String str[] = line.split(" ");
					execRunning.setText(str[0]+" "+str[1]);
					if(line.contains("Passed")) {
						pass += 1;total += 1;
						//executionResults.add(new ExecutionResult(Integer.parseInt(str[1].split("/")[0]), "pass"));
					} else if(line.contains("Failed")) {
						fail +=1; total +=1;
					} else if(line.contains("Deadlocked")) {
						dead += 1; total +=1;
					} else if(line.contains("Error")) {
						error += 1; total +=1;
					}
					
				} else if(line.contains("finished")) {
					// All tests/executions finished
					running.setText("All Tests Completed");
					execRunning.setText("All Executions Completed");
				}

				//update result text boxes
				if(passAmntIsFraction) {
					passAmnt.setText((int)pass+"/"+(int)total);
				} else {
					passAmnt.setText(String.format("%.2f", pass/total*100)+"%");
				}

				if(failAmntIsFraction) {
					failAmnt.setText((int)fail+"/"+(int)total);
				} else {
					failAmnt.setText(String.format("%.2f", fail/total*100)+"%");
				}

				if(errAmntIsFraction) {
					errAmnt.setText((int)error+"/"+(int)total);
				} else {
					errAmnt.setText(String.format("%.2f", error/total*100)+"%");
				}

				if(deadAmntIsFraction) {
					deadAmnt.setText((int)dead+"/"+(int)total);
				} else {
					deadAmnt.setText(String.format("%.2f", dead/total*100)+"%");
				}
			  }
		});


	}

	public void clearResultWidgets() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				pass = 0; fail = 0; error = 0; dead = 0; total = 0;
				
				if(results != null) {
					results.clear();
				}
				
				progress.setSelection(0);
				execProgress.setSelection(0);
				
				resultTree.deselectAll();
				
				canvas.redraw();
				detailCanvas.redraw();
				
				resultTree.removeAll();
				testDetails.clearSelection();

				if(passAmntIsFraction) {
					passAmnt.setText("000/000");
				} else {
					passAmnt.setText("0.00%");
				}

				if(failAmntIsFraction) {
					failAmnt.setText("000/000");
				} else {
					failAmnt.setText("0.00%");
				}

				if(errAmntIsFraction) {
					errAmnt.setText("000/000");
				} else {
					errAmnt.setText("0.00%");
				}

				if(deadAmntIsFraction) {
					deadAmnt.setText("000/000");
				} else {
					deadAmnt.setText("0.00%");
				}
			}
		});
	}

	public void clearOutputFile() {
		try {
			PrintWriter pw = new PrintWriter("syncTestOutput.txt");
			pw.write(""); pw.close(); //should clear the file?
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void setFocus() {}
}
