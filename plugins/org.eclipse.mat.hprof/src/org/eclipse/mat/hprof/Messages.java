/*******************************************************************************
 * Copyright (c) 2010, 2023 SAP AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     SAP AG - initial API and implementation
 *     IBM Corporation - multiple heap dumps
 *******************************************************************************/
package org.eclipse.mat.hprof;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS
{
    private static final String BUNDLE_NAME = "org.eclipse.mat.hprof.messages"; //$NON-NLS-1$
    public static String AbstractParser_Error_IllegalType;
    public static String AbstractParser_Error_InvalidHPROFHeader;
    public static String AbstractParser_Error_NotHeapDump;
    public static String AbstractParser_Error_UnknownHPROFVersion;
    public static String AbstractParser_Error_UnsupportedHPROFVersion;
    public static String AbstractParser_GuessedRecordLength;
    public static String EnhancerRegistry_ErrorCreatingParser;
    public static String EnhancerRegistry_ErrorCreatingRuntime;
    public static String ExportHprof_AvoidExample;
    public static String ExportHprof_ExportTo;
    public static String ExportHprof_PrepareClasses;
    public static String ExportHprof_PrepareGCRoots;
    public static String ExportHprof_PrepareObjects;
    public static String ExportHprof_PrepareThreadStacks;
    public static String ExportHprof_DumpClasses;
    public static String ExportHprof_DumpGCRoots;
    public static String ExportHprof_DumpObjects;
    public static String ExportHprof_DumpStrings;
    public static String ExportHprof_DumpThreadStacks;
    public static String ExportHprof_RemapProperties;
    public static String ExportHprof_SegmentSizeMismatch;
    public static String ExportHprof_SegmentTooLong;
    public static String GZIPInputStream2_BadHeaderCRC;
    public static String GZIPInputStream2_BadHeaderFlag;
    public static String GZIPInputStream2_BadTrailerCRC;
    public static String GZIPInputStream2_BadTrailerLength;
    public static String GZIPInputStream2_NotAGzip;
    public static String GZIPInputStream2_NotDeflate;
    public static String GZIPInputStream2_TruncatedComment;
    public static String GZIPInputStream2_TruncatedExtra;
    public static String GZIPInputStream2_TruncatedHeader;
    public static String GZIPInputStream2_TruncatedHeaderCRC;
    public static String GZIPInputStream2_TruncatedName;
    public static String HprofIndexBuilder_ExtractingObjects;
    public static String HprofIndexBuilder_Parsing;
    public static String HprofIndexBuilder_Scanning;
    public static String HprofIndexBuilder_Writing;
    public static String HprofParserHandlerImpl_ClassNotFoundInAddressIndex;
    public static String HprofParserHandlerImpl_DiscardedObjects;
    public static String HprofParserHandlerImpl_Error_ExpectedClassSegment;
    public static String HprofParserHandlerImpl_Error_MultipleClassInstancesExist;
    public static String HprofParserHandlerImpl_HeapContainsObjects;
    public static String HprofRandomAccessParser_Error_DumpIncomplete;
    public static String HprofRandomAccessParser_Error_DuplicateClass;
    public static String HprofRandomAccessParser_Error_IllegalDumpSegment;
    public static String HprofRandomAccessParser_Error_MissingClass;
    public static String HprofRandomAccessParser_Error_MissingFakeClass;
    public static String IPositionInputStream_mark;
    public static String IPositionInputStream_reset;
    public static String IPositionInputStream_seek;
    public static String JMapHeapDumpProvider_ErrorCreatingDump;
    public static String JMapHeapDumpProvider_HeapDumpNotCreated;
    public static String JMapHeapDumpProvider_WaitForHeapDump;
    public static String JMapHeapDumpProvider_ListProcesses;
    public static String JMapHeapDumpProvider_CompressingDump;
    public static String LocalJavaProcessesUtils_ErrorGettingProcesses;
    public static String LocalJavaProcessesUtils_ErrorGettingProcessListJPS;
    public static String Pass1Parser_DetectedCompressedReferences;
    public static String Pass1Parser_Error_IllegalRecordLength;
    public static String Pass1Parser_Error_IllegalType;
    public static String Pass1Parser_Error_InvalidHeapDumpFile;
    public static String Pass1Parser_Error_invalidHPROFFile;
    public static String Pass1Parser_Error_NoHeapDumpIndexFound;
    public static String Pass1Parser_Error_SupportedDumps;
    public static String Pass1Parser_Error_UnresolvedName;
    public static String Pass2Parser_Error_HandleMustCreateFakeClassForName;
    public static String Pass2Parser_Error_HandlerMustCreateFakeClassForAddress;
    public static String Pass2Parser_Error_InsufficientBytesRead;
    public static String Pass1Parser_Info_UsingDumpIndex;
    public static String Pass1Parser_Info_WroteThreadsTo;
    public static String Pass1Parser_Error_WritingThreadsInformation;
    public static String Pass1Parser_ExceptionReadingSubrecord;
    public static String Pass1Parser_UnexpectedEndPosition;
    public static String Pass1Parser_UnexpectedRecord;
    public static String Pass1Parser_UnloadClassNotFound;
    public static String Pass1Parser_GuessingLengthOverflow;
    public static String Pass1Parser_HeapDumpCreated;
    public static String Pass1Parser_HeapDumpsFound;
    public static String HPROFPreferencePage_DiscardOffset;
    public static String HPROFPreferencePage_DiscardPattern;
    public static String HPROFPreferencePage_DiscardPercentage;
    public static String HPROFPreferencePage_DiscardSeed;
    public static String HPROFPreferencePage_EnableDiscard;
    public static String HPROFPreferences_Description;
    public static String HPROFPreferences_Strictness;
    public static String HPROFPreferences_Strictness_Stop;
    public static String HPROFPreferences_Strictness_Warning;
    public static String HPROFPreferences_Strictness_Permissive;
    public static String HPROFStrictness_Unhandled_Preference;
    public static String HPROFStrictness_Stopped;
    public static String HPROFPreferences_Additional_Class_References;
    public static String HPROFPreferencePage_MethodsAsClasses;
    public static String HPROFPreferencePage_NoMethods;
    public static String HPROFPreferencePage_OnlyStackFrames;
    public static String HPROFPreferencePage_RunningMethods;

    static
    {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages()
    {}
}
