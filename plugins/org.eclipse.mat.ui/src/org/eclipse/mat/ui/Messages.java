/*******************************************************************************
 * Copyright (c) 2010, 2021 SAP AG, IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *     IBM Corporation - multiple snapshots in a file
 *******************************************************************************/
package org.eclipse.mat.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.eclipse.mat.ui.messages"; //$NON-NLS-1$
    public static String ArgumentsTable_AddressNotComplete;
    public static String ArgumentsTable_Argument;
    public static String ArgumentsTable_Encountered;
    public static String ArgumentsTable_InvalidAddress;
    public static String ArgumentsTable_InvalidClassNamePattern;
    public static String ArgumentsTable_isMandatory;
    public static String ArgumentsTable_ProvidePattern;
    public static String ArgumentsTable_selectedRows;
    public static String ArgumentsTable_Value;
    public static String ArgumentsTable_WasExpecting;
    public static String ArgumentsWizardPage_QueryArguments;
    public static String BundlesPane_GroupBy;
    public static String CheckBoxEditor_asRetainedSet;
    public static String CheckBoxEditor_asRetainedSetAdditional;
    public static String CheckBoxEditor_includeClassInstance;
    public static String CheckBoxEditor_includeLoadedObjects;
    public static String CheckBoxEditor_includeLoadedObjectsAdditional;
    public static String CheckBoxEditor_includeSubclasses;
    public static String CheckBoxEditor_includeSubclassesAdditional;
    public static String CheckBoxEditor_verbose;
    public static String CheckBoxEditor_verboseAdditional;
    public static String CompareBasketView_ClearButtonLabel;
	public static String CompareBasketView_ClearTooltip;
	public static String CompareBasketView_CompareButtonLabel;
	public static String CompareBasketView_ComparedTablesResultTitle;
	public static String CompareBasketView_CompareTooltip;
	public static String CompareBasketView_HeapDumpColumnHeader;
	public static String CompareBasketView_MoveDownButtonLabel;
	public static String CompareBasketView_MoveDownTooltip;
	public static String CompareBasketView_MoveUpButtonLabel;
	public static String CompareBasketView_MoveUpTooltip;
	public static String CompareBasketView_RemoveButtonLabel;
	public static String CompareBasketView_RemoveTooltip;
	public static String CompareBasketView_ResultsToBeComparedColumnHeader;
    public static String CompareTablesPane_AbsoluteValues;
    public static String CompareTablesPane_ChooseDiffOptionTooltip;
    public static String CompareTablesPane_ChooseOperation;
    public static String CompareTablesPane_DifferenceToBaseTable;
    public static String CompareTablesPane_DifferenceToPrecedingTable;
    public static String CompareTablesPane_PercentageDifferenceToBaseTable;
    public static String CompareTablesPane_PercentageDifferenceToPrecedingTable;
    public static String CompareTablesPane_SelectDisplayedColumnsTooltip;
	public static String CompareTablesPane_Setop_All;
    public static String CompareTablesPane_Setop_Difference;
    public static String CompareTablesPane_Setop_Intersection;
    public static String CompareTablesPane_Setop_None;
    public static String CompareTablesPane_Setop_ReverseDifference;
    public static String CompareTablesPane_Setop_SymmetricDifference;
    public static String CompareTablesPane_Setop_Union;
    public static String Copy_ErrorInExport;
    public static String CopyActions_CopyingToClipboard;
    public static String CopyOQL_Copying;
    public static String CopyOQL_TooBig;
    public static String DerivedDataJob_CalculatingRetainedSizes;
    public static String DominatorPane_Group;
    public static String DominatorPane_Info;
    public static String DominatorPane_WholeTreeWillBeGrouped;
    public static String ErrorHelper_ClassNotFound;
    public static String ErrorHelper_DefinitionNotFound;
    public static String ErrorHelper_Error;
    public static String ErrorHelper_Exception;
    public static String ErrorHelper_ExceptionWithMessage;
    public static String ErrorHelper_Information;
    public static String ErrorHelper_InternalError;
    public static String ErrorHelper_InternalRuntimeError;
    public static String ErrorHelper_NoSuchMethod;
    public static String ExecuteInspectionHandler_NoActiveEditor;
    public static String ExecuteInspectionHandler_NoActivePage;
    public static String ExecuteInspectionHandler_NoActiveWorkbenchWindow;
    public static String ExecuteInspectionHandler_NotHeapEditor;
    public static String ExecuteInspectionHandler_UnknownInspection;
    public static String ExportActions_AlreadyExists;
    public static String ExportActions_CsvFiles;
    public static String ExportActions_Export;
    public static String ExportActions_ExportCSV;
    public static String ExportActions_ExportHTML;
    public static String ExportActions_ExportTXT;
    public static String ExportActions_ExportToCSV;
    public static String ExportActions_ExportToHTML;
    public static String ExportActions_ExportToTxt;
    public static String ExportActions_PlainText;
    public static String ExportActions_PlainText2;
    public static String ExportActions_ZippedWebPage;
    public static String FieldsContentProvider_Displayed;
    public static String FileOpenDialogEditor_ChooseFile;
    public static String GettingStartedWizard_ChooseOneOfReports;
    public static String GettingStartedWizard_ComponentReport;
    public static String GettingStartedWizard_ComponentReportDescription;
    public static String GettingStartedWizard_ExistingReportsLocation;
    public static String GettingStartedWizard_GettingStarted;
    public static String GettingStartedWizard_GettingStartedWizard;
    public static String GettingStartedWizard_LeakSuspectReport;
    public static String GettingStartedWizard_LeakSuspectReportDescription;
    public static String GettingStartedWizard_Package;
    public static String GettingStartedWizard_ReOpenExistingReport;
    public static String GettingStartedWizard_ReOpenExistingReports;
    public static String GettingStartedWizard_ReOpenReport;
    public static String GettingStartedWizard_Report;
    public static String GettingStartedWizard_SelectClasses;
    public static String GettingStartedWizard_ShowThisDialog;
    public static String GettingStartedWizard_SpecifyRegularExpression;
    public static String HeapEditor_UnsupportedEditorInput;
    public static String HeapEditor_UnsupportedScheme;
    public static String HeapEditorContributions_OpenOverviewPane;
    public static String HeapEditorPane_EditorInputMustBeOfType;
    public static String HistogramPane_CalculatingIntersectingHistograms;
    public static String HistogramPane_CompareAgainst;
    public static String HistogramPane_CompareToAnotherHeapDump;
    public static String HistogramPane_GroupByClass;
    public static String HistogramPane_GroupByClassLoader;
    public static String HistogramPane_GroupByPackage;
    public static String HistogramPane_GroupBySuperclass;
    public static String HistogramPane_IllegalType;
    public static String HistogramPane_NoOtherHeapDump;
    public static String HistogramPane_SelectBaseline;
    public static String HistogramPane_SelectHeapDump;
    public static String ImportReportAction_ImportReport;
    public static String ImportReportAction_MemoryAnalyzerReports;
    public static String ImportReportAction_OpenReport;
    public static String ImportReportAction_Report;
    public static String InspectorView_Attributes;
    public static String InspectorView_ClassHierarchy;
    public static String InspectorView_ErrorUpdatingInspector;
    public static String InspectorView_GoInto;
    public static String InspectorView_LinkWithSnapshot;
    public static String InspectorView_Name;
    public static String InspectorView_noGCRoot;
    public static String InspectorView_PinTab;
    public static String InspectorView_retainedSize;
    public static String InspectorView_shallowSize;
    public static String InspectorView_Statics;
    public static String InspectorView_Type;
    public static String InspectorView_UpdateObjectDetails;
    public static String InspectorView_Value;
    public static String LazyFields_ErrorReadingArrayDetails;
    public static String LinkEditor_moreOptions;
    public static String LinkEditor_simpleMode;
    public static String MemoryAnalyserPlugin_InternalError;
    public static String MultiPaneEditor_Cancel;
    public static String MultiPaneEditor_Close;
    public static String MultiPaneEditor_CloseAll;
    public static String MultiPaneEditor_CloseOthers;
    public static String MultiPaneEditor_CloseToLeft;
    public static String MultiPaneEditor_CloseToRight;
    public static String MultiPaneEditor_Failed_to_open;
    public static String MultiPaneEditor_MoveTabLeft;
    public static String MultiPaneEditor_MoveTabRight;
    public static String MultiPaneEditor_Opening;
    public static String MultiPaneEditor_UnsupportedEditorInput;
    public static String MultiPaneEditor_UnsupportedScheme;
    public static String NavigatorView_ViewNotAvailable;
    public static String NavigatorViewPage_Activate;
	public static String NavigatorViewPage_AddToCompareBasketMenuItem;
    public static String NavigatorViewPage_Close;
    public static String NavigatorViewPage_CloseBranch;
    public static String NavigatorViewPage_RemoveFromList;
    public static String OpenHelpPageAction_Help;
    public static String OpenIconAssistAction_ArrayObject;
    public static String OpenIconAssistAction_ClassLoaderObject;
    public static String OpenIconAssistAction_ClassObject;
    public static String OpenIconAssistAction_ExecuteQuery;
    public static String OpenIconAssistAction_HeapObjects;
    public static String OpenIconAssistAction_IndicatorsAdded;
    public static String OpenIconAssistAction_InstancesGroupedByClass;
    public static String OpenIconAssistAction_MixedInstancesGroupedByClass;
    public static String OpenIconAssistAction_OldInstancesGroupedByClass;
    public static String OpenIconAssistAction_OtherObject;
    public static String OpenIconAssistAction_Others;
    public static String OpenIconAssistAction_Package;
    public static String OpenIconAssistAction_QueryViews;
    public static String OpenIconAssistAction_ReferenceAbove;
    public static String OpenIconAssistAction_ReferenceBelow;
    public static String OpenIconAssistAction_ThisObjectIsGCRoot;
    public static String OpenObjectByIdAction_AddressIsNotHexNumber;
    public static String OpenObjectByIdAction_AddressMustBeHexNumber;
    public static String OpenObjectByIdAction_ErrorGettingHeapDump;
    public static String OpenObjectByIdAction_ErrorOpeningObject;
    public static String OpenObjectByIdAction_ErrorReadingObject;
    public static String OpenObjectByIdAction_FindObjectByAddress;
    public static String OpenObjectByIdAction_NoObjectWithAddress;
    public static String OpenObjectByIdAction_ObjectAddress;
    public static String OpenOQLStudioAction_OpenOQLStudio;
    public static String OpenSampleHeapDumpAction_ErrorOpeningEditor;
    public static String OpenSampleHeapDumpAction_NoEditorToOpen;
    public static String OpenSnapshot_AllFiles;
    public static String OpenSnapshot_AllKnownFormats;
    public static String OpenSnapshot_FileAlreadyExists;
    public static String OpenSnapshot_FileMustHaveExtension;
    public static String OpenSnapshot_FileNotFound;
    public static String OpenSnapshot_FilesNotFound;
    public static String OpenSnapshot_OpenSnapshot;
    public static String OpenSnapshot_RenameHeapDump;
    public static String OpenSnapshot_Warning;
    public static String OpenSnapshotAction_ErrorOpeningFile;
    public static String OpenSnapshotAction_Message;
    public static String OpenSnapshotAction_SelectWorkspace;
    public static String OQLPane_ErrorExecutingQuery;
    public static String OQLPane_ExecutedQuery;
    public static String OQLPane_ExecuteQuery;
    public static String OQLPane_F1ForHelp;
    public static String OQLPane_PaneNotFound;
    public static String OQLPane_ProblemReported;
    public static String OverviewPane_Actions;
    public static String OverviewPane_AdditionalInfo;
    public static String OverviewPane_BiggestObjectsByRetainedSIze;
    public static String OverviewPane_Classes;
    public static String OverviewPane_ClassLoader;
    public static String OverviewPane_ComponentReportInfo;
    public static String OverviewPane_Details;
    public static String OverviewPane_DominatorTreeInfo;
    public static String OverviewPane_DuplicateClassesInfo;
    public static String OverviewPane_ExtractingBigObjects;
    public static String OverviewPane_HistogramInfo;
    public static String OverviewPane_Objects;
    public static String OverviewPane_Overview;
    public static String OverviewPane_Reports;
    public static String OverviewPane_Size;
    public static String OverviewPane_StepByStep;
    public static String OverviewPane_TopConsumersInfo;
    public static String ParseHeapDumpJob_ParsingHeapDumpFrom;
    public static String Path2GCRootsPane_FetchingNext;
    public static String Path2GCRootsPane_FetchNextPaths;
    public static String Path2GCRootsPane_FoundSoFar;
    public static String Path2GCRootsPane_NoMorePaths;
    public static String Path2GCRootsPane_ReadingNext;
    public static String Path2GCRootsPane_Status;
    public static String PopupMenu_NONE;
	public static String ProviderArgumentsWizzardPage_HeapDumpProviderArgumentsTitle;
    public static String ProviderConfigurationDialog_ApplyButtonLabel;
	public static String ProviderConfigurationDialog_AvailableProvidersLabel;
	public static String ProviderConfigurationDialog_ConfigurableParameteresLabel;
	public static String ProviderConfigurationDialog_ConfigureProvidersDialogTitle;
	public static String ProviderConfigurationDialog_DescriptionColumnHeader;
	public static String ProviderConfigurationDialog_IllegalArgumentErrorMessage;
	public static String ProviderConfigurationDialog_MissingParameterErrorMessage;
	public static String ProviderConfigurationDialog_NameColumnHeader;
	public static String QueryAllProvider_AllQueries;
    public static String QueryBrowserPopup_Categories;
    public static String QueryBrowserPopup_PressCtrlEnter;
    public static String QueryBrowserPopup_StartTyping;
    public static String QueryContextHelp_Arguments;
    public static String QueryContextHelp_Usage;
    public static String QueryContextMenu_context;
    public static String QueryContextMenu_CopySelectionToTheClipboard;
    public static String QueryContextMenu_Details;
    public static String QueryContextMenu_Processing;
    public static String QueryContextMenu_selectionOf;
    public static String QueryContextMenu_Selection;
    public static String QueryContextMenu_SubjectMustBeOfType;
    public static String QueryDropDownMenuAction_All;
    public static String QueryDropDownMenuAction_History;
    public static String QueryDropDownMenuAction_OpenQueryBrowser;
    public static String QueryDropDownMenuAction_SeachQueriesByName;
    public static String QueryDropDownMenuAction_SearchQueries;
    public static String QueryExecution_NoHTMLOutput;
    public static String QueryExecution_NoResult;
    public static String QueryHistoryProvider_History;
    public static String QueryRegistryProvider_Uncategorized;
    public static String QueryResultPane_InfoMessage;
    public static String QueryResultPane_QueryResult;
    public static String QueryResultPane_ShowAsHistogram;
    public static String QueryTextResultPane_BrowserTitle;
    public static String QueryTextResultPane_FailedBrowser;
    public static String QueryTextResultPane_FailedBrowser2;
    public static String QueryTextResultPane_Text;
    public static String QueryTextResultPane_UnableToMapAddress;
    public static String RefinedResultViewer_BlockingWarning;
    public static String RefinedResultViewer_CalculateRetainedSize;
	public static String RefinedResultViewer_Columns;
	public static String RefinedResultViewer_ConfigureColumns;
    public static String RefinedResultViewer_CustomExpand;
    public static String RefinedResultViewer_EditFilter;
    public static String RefinedResultViewer_EnterNumber;
    public static String RefinedResultViewer_ExpandAll;
    public static String RefinedResultViewer_ExpandToLimit;
    public static String RefinedResultViewer_Export;
    public static String RefinedResultViewer_Next25;
    public static String RefinedResultViewer_notValidNumber;
    public static String RefinedResultViewer_RetrieveViewElements;
    public static String RefinedResultViewer_Sort_By;
	public static String RefinedResultViewer_Sorting;
    public static String RefinedResultViewer_updating;
    public static String RefinedTreeViewer_CalculateTotals;
    public static String RunExternalReportAction_OpenReportDefinition;
    public static String RunExternalReportAction_ReportDefinitions;
    public static String RunExternalReportAction_RunReport;
    public static String RunReportsDropDownAction_RunExpertSystemTest;
    public static String RuntimeSelector_Java_Options;
    public static String RuntimeSelector_Java_Version;
    public static String RuntimeSelector_Runtime_Description;
    public static String RuntimeSelector_SelectSnapshot;
    public static String RuntimeSelector_SelectSnapshotTitle;
    public static String RuntimeSelector_Snapshot_Identifier;
    public static String SaveValueAsQuery_FileExists;
    public static String SaveValueAsQuery_Overwrite;
    public static String SaveValueAsQuery_Saving;
    public static String SaveValueAsQuery_SavingBinaryValue;
    public static String SaveValueAsQuery_UnrecognizedPrimitiveArrayType;
    public static String SearchOnTyping_Exception;
    public static String SearchOnTyping_searching;
    public static String SnapshotHistoryService_NonExistingSnapshotsWillBeDeleted;
    public static String SnapshotHistoryService_SnapshotsDoNotExist;
    public static String SnapshotHistoryView_AreYouSure4ManyFiles;
    public static String SnapshotHistoryView_AreYouSure4OneFile;
    public static String SnapshotHistoryView_CopyFilename;
    public static String SnapshotHistoryView_ConfirmDeletion;
    public static String SnapshotHistoryView_DeleteFile;
    public static String SnapshotHistoryView_DeleteFromHistory;
    public static String SnapshotHistoryView_DeleteIndexFiles;
    public static String SnapshotHistoryView_DeleteInFileSystem;
    public static String SnapshotHistoryView_ErrorDeletingFiles;
    public static String SnapshotHistoryView_ExploreFileSystem;
    public static String SnapshotHistoryView_ExploreInFileSystem;
    public static String SnapshotHistoryView_FileDoesNotExist;
    public static String SnapshotHistoryView_FileDoesNotExistAnymore;
    public static String SnapshotHistoryView_Open;
    public static String SnapshotHistoryView_OperationNotImplemented;
    public static String SnapshotHistoryView_RecentlyUsedFiles;
    public static String SnapshotHistoryView_RemoveFromList;
    public static String SnapshotHistoryView_SelectedFileDoesNotExist;
    public static String SnapshotHistoryView_TryToUseGnome;
    public static String SnapshotSelectionEditor_FileDoesNotExist;
    public static String TableResultPane_GroupResultBy;
    public static String TableResultPane_Info;
    public static String TableResultPane_NoGrouping;
    public static String TableResultPane_onColumn;
    public static String TableResultPane_TableIsFiltered;
    public static String TextEditor_EnterAddress;
    public static String TextEditor_EnterOQLQuery;
    public static String TextEditor_EnterPattern;
    public static String TextViewPane_TextDisplay;

	public static String OverviewPane_Histogram;
	public static String OverviewPane_DominatorTree;
	public static String OverviewPane_TopConsumers;
	public static String OverviewPane_DuplicateClasses;
	public static String OverviewPane_ComponentReport;
    public static String OverviewPane_NoPieChartAvailable;
	public static String InspectorView_GCroot;
	
	public static String AcquireDialog_BrowseButton;
	public static String AcquireDialog_ChooseDestinationDirectory;
    public static String AcquireDialog_ChooseDestinationDirectoryAndFile;
    public static String AcquireDialog_ChooseDestinationDirectoryMessage;
    public static String AcquireDialog_ChooseProcess;
	public static String AcquireDialog_ColumnDescription;
	public static String AcquireDialog_ColumnPID;
	public static String AcquireDialog_ConfigureButtonLabel;
	public static String AcquireDialog_DialogDescription;
	public static String AcquireDialog_DialogName;
	public static String AcquireDialog_FileExists;
    public static String AcquireDialog_FileIsDirectory;
    public static String AcquireDialog_HeapDumpProviderColumnHeader;
    public static String AcquireDialog_InvalidFilenameTemplate;
	public static String AcquireDialog_RefreshButtonLabel;
	public static String AcquireDialog_SaveFileLocation;
	public static String AcquireDialog_SaveLocation;

	

    public static String AcquireSnapshotAction_AcquireDialogName;
	public static String AcquireSnapshotAction_Confirmation;
	public static String AcquireSnapshotAction_DirectoryDoesntExist;
	public static String AcquireSnapshotAction_FailedToCreateProvider;
	public static String AcquireSnapshotAction_FileAlreadyExists;
	public static String AcquireSnapshotAction_IllegalTypeErrorMessage;
	public static String AcquireSnapshotAction_MissingParameterErrorMessage;
	public static String AcquireSnapshotAction_NoProviderError;
	public static String AcquireSnapshotAction_UnableToCreateDirectory;
	public static String AcquireSnapshotAction_UnableToOpenEditor;
	public static String AcquireSnapshotAction_UnexpectedException;



	public static String UIPreferencePage_HideGettingStartedWizard;
    public static String UIPreferencePage_KeepUnreachableObjects;
    public static String UIPreferencePage_PreferencesSubtitle;
    public static String UIPreferencePage_HideQueryHelp;
    public static String UIPreferencePage_BytesDisplay;
    public static String UIPreferencePage_BytesDisplay_Bytes;
    public static String UIPreferencePage_BytesDisplay_Kilobytes;
    public static String UIPreferencePage_BytesDisplay_Megabytes;
    public static String UIPreferencePage_BytesDisplay_Gigabytes;
    public static String UIPreferencePage_BytesDisplay_Smart;
    public static String UIPreferencePage_HideWelcomeScreen;
    public static String UIPreferencePage_DiscardOffset;
    public static String UIPreferencePage_DiscardPattern;
    public static String UIPreferencePage_DiscardPercentage;
    public static String UIPreferencePage_DiscardSeed;
    public static String UIPreferencePage_DiscardEnable;



    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {}
}
