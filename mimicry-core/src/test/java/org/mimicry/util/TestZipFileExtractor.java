package org.mimicry.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mimicry.util.IOUtils;
import org.mimicry.util.ZipFileExtractor;

public class TestZipFileExtractor
{

	private static final String			SIMPLE_FILE_NAME	= "simple_file.txt";
	private static final List<String>	ALL_FILES			= Arrays.asList( "dir/", "dir/anotherFile.txt",
																	"dir/dir2/", "dir/dir2/anotherFile.txt",
																	"emptyDir/", SIMPLE_FILE_NAME );
	private static final List<String>	DIR_FILES			= Arrays.asList( "dir/", "dir/anotherFile.txt",
																	"dir/dir2/", "dir/dir2/anotherFile.txt" );
	private static final List<String>	SIMPLE_FILE			= Arrays.asList( SIMPLE_FILE_NAME );

	private File						destinationDir;
	private ZipFileExtractor			extractor;

	@Before
	public void setUp() throws ZipException, IOException
	{
		extractor = new ZipFileExtractor( new File( "src/test/resources/sample.zip" ) );
		destinationDir = IOUtils.createTempDir( "unit_", "_test" );
	}

	@After
	public void tearDown()
	{
		extractor.close();
	}

	private Set<String> extract( String pattern ) throws IOException
	{
		return extractor.extract( pattern, destinationDir );
	}

	private Set<String> extractAll() throws IOException
	{
		return extractor.extractAll( destinationDir );
	}

	private void assertFile( String name )
	{
		File file = new File( destinationDir, name );
		assertTrue( "File '" + name + "' has not been extracted.", file.exists() );
	}

	private void assertEqualsIgnoreOrder( Collection<String> expected, Collection<String> actual )
	{
		assertEquals( expected.size(), actual.size() );
		for ( String s : expected )
		{
			assertTrue( actual.contains( s ) );
		}
		for ( String s : actual )
		{
			assertTrue( expected.contains( s ) );
		}
	}

	@Test
	public void testExtractAllWithPattern() throws IOException
	{
		Set<String> extractedFiles = extract( ZipFileExtractor.EVERTHING_PATTERN );
		for ( String file : extractedFiles )
		{
			assertFile( file );
		}
		assertEqualsIgnoreOrder( ALL_FILES, extractedFiles );
	}

	@Test
	public void testExtractAll() throws IOException
	{
		Set<String> extractedFiles = extractAll();
		for ( String file : extractedFiles )
		{
			assertFile( file );
		}
		assertEqualsIgnoreOrder( ALL_FILES, extractedFiles );
	}

	@Test
	public void testExtractSimpleFile() throws IOException
	{
		Set<String> extractedFiles = extract( SIMPLE_FILE_NAME );
		for ( String file : extractedFiles )
		{
			assertFile( file );
		}
		assertEqualsIgnoreOrder( SIMPLE_FILE, extractedFiles );
	}

	@Test
	public void testExtractMatchingPatternWithoutSlash() throws IOException
	{
		Set<String> extractedFiles = extract( "dir" );
		for ( String file : extractedFiles )
		{
			assertFile( file );
		}
		assertEqualsIgnoreOrder( DIR_FILES, extractedFiles );
	}

	@Test
	public void testExtractMatchingPatternWithSlash() throws IOException
	{
		Set<String> extractedFiles = extract( "dir/" );
		for ( String file : extractedFiles )
		{
			assertFile( file );
		}
		assertEqualsIgnoreOrder( DIR_FILES, extractedFiles );
	}
}
