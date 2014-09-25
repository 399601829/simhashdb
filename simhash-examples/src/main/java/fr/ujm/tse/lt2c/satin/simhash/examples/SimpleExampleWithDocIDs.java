/**
 *
 */
package fr.ujm.tse.lt2c.satin.simhash.examples;

import java.io.File;
import java.util.Arrays;

import com.google.common.hash.Hashing;

import fr.ujm.tse.lt2c.satin.simhash.knn.ConversionMethod;
import fr.ujm.tse.lt2c.satin.simhash.knn.InMemorySimHashDBwithDocIDs;
import fr.ujm.tse.lt2c.satin.simhash.text.transform.Conversion;
import fr.ujm.tse.lt2c.satin.simhash.text.transform.preprocessing.counting.SimpleCounting;
import fr.ujm.tse.lt2c.satin.text.simhash.text.test.SimpleTokenizer;

/**
 * @author Julien
 *
 */
public class SimpleExampleWithDocIDs {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final ConversionMethod method = new Conversion(new SimpleTokenizer(),
				new SimpleCounting());
		final String s1 = "The cat is playing outside.";
		final String s2 = "John is playing outside.";
		final String s3 = "This has nothing to do with the previous sentences";
		System.out.println("Three sentences to test");
		System.out.println("S1 : " + s1);
		System.out.println("S2 : " + s2);
		System.out.println("S3 : " + s3);
		// Converting
		System.out.println();
		System.out.println("Converting into map<string,double>");
		System.out.println(method.convert(s1));
		System.out.println(method.convert(s2));
		System.out.println(method.convert(s3));
		final InMemorySimHashDBwithDocIDs db = new InMemorySimHashDBwithDocIDs(
				Hashing.murmur3_128());
		System.out.println("Adding documents to DB");
		db.putDocument(method.convert(s1), "S1");
		db.putDocument(method.convert(s2), "S2");
		db.putDocument(method.convert(s3), "S3");

		System.out.println();
		System.out.println("--- DB Internals ---");
		System.out.println(db.viewDB());

		System.out.println();
		System.out.println("---- 3NN of s1 (documents ids)");
		// Takes everything
		int[] nns = db.kNearestNeighbors(method.convert(s1), db.size());
		System.out.println(Arrays.toString(nns));

		System.out.println();
		System.out.println("Saving to disk");
		final File f = new File("saveExampleDB.db");
		db.saveHashes(f);

		System.out.println();
		System.out.println("Creating new db");
		final InMemorySimHashDBwithDocIDs newdb = InMemorySimHashDBwithDocIDs
				.loadFromFileSystem(f, Hashing.murmur3_128());

		System.out.println();
		System.out.println("--- New DB Internals ---");
		System.out.println(newdb.viewDB());

		System.out.println();
		System.out.println("---- 3NN of s1 (documents ids) on the new db");
		// Takes everything
		nns = newdb.kNearestNeighbors(method.convert(s1), newdb.size());
		System.out.println(Arrays.toString(nns));

		System.out.println("Deleting saved DB");
		f.delete();

	}
}