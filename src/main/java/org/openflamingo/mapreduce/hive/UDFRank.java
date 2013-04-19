package org.openflamingo.mapreduce.hive;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory.ObjectInspectorOptions;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorUtils;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;


@Description(name = "rank", value = "_FUNC_(col1, col2, .., coln) - Returns the rank of current row, "
	+ "when some column differs from one row to the next the rank is reset to 1.")
public class UDFRank extends GenericUDF {

	private long counter;
	private Object[] previousKey;
	private ObjectInspector[] ois;

	@Override
	public Object evaluate(DeferredObject[] currentKey) throws HiveException {
		if (!sameAsPreviousKey(currentKey)) {
			this.counter = 0;
			copyToPreviousKey(currentKey);
		}

		return new Long(++this.counter);
	}

	@Override
	public String getDisplayString(String[] currentKey) {
		return "Rank-Udf-Display-String";
	}

	@Override
	public ObjectInspector initialize(ObjectInspector[] arg0) throws UDFArgumentException {
		ois = arg0;
		return PrimitiveObjectInspectorFactory.javaLongObjectInspector;
	}

	/**
	 * This will help us copy objects from currrentKey to previousKeyHolder.
	 *
	 * @param currentKey
	 * @throws org.apache.hadoop.hive.ql.metadata.HiveException
	 */
	private void copyToPreviousKey(DeferredObject[] currentKey) throws HiveException {
		if (currentKey != null) {
			previousKey = new Object[currentKey.length];
			for (int index = 0; index < currentKey.length; index++) {
				previousKey[index] = ObjectInspectorUtils
					.copyToStandardObject(currentKey[index].get(), this.ois[index]);

			}
		}
	}

	/**
	 * This will help us compare the currentKey and previousKey objects.
	 *
	 * @param currentKey
	 * @return - true if both are same else false
	 * @throws org.apache.hadoop.hive.ql.metadata.HiveException
	 */
	private boolean sameAsPreviousKey(DeferredObject[] currentKey) throws HiveException {
		boolean status = false;

		//if both are null then we can classify as same
		if (currentKey == null && previousKey == null) {
			status = true;
		}

		//if both are not null and there length as well as
		//individual elements are same then we can classify as same
		if (currentKey != null && previousKey != null && currentKey.length == previousKey.length) {
			for (int index = 0; index < currentKey.length; index++) {

				// avoid calling previousKey[index].getClass() when previousKey[index] is null
				if (previousKey[index] != null) {
					if (ObjectInspectorUtils.compare(currentKey[index].get(), this.ois[index], previousKey[index],
						ObjectInspectorFactory.getReflectionObjectInspector(previousKey[index].getClass(), ObjectInspectorOptions.JAVA)) != 0) {

						return false;
					} else {
						continue;
					}
					// Both objects are null
				} else if (currentKey[index].get() == null) {
					continue;
					// current is not null but previous is
				} else {
					return false;
				}

			}
			status = true;
		}
		return status;
	}
}