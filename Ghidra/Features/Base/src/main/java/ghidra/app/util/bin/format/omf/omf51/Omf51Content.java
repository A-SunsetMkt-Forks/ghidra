/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.util.bin.format.omf.omf51;

import java.io.IOException;

import ghidra.app.util.bin.BinaryReader;
import ghidra.app.util.bin.format.omf.*;
import ghidra.program.model.data.*;
import ghidra.util.exception.DuplicateNameException;

public class Omf51Content extends OmfRecord {

	private int segId;
	private int offset;
	private long dataIndex;
	private int dataSize;

	boolean largeSegmentId;

	/**
	 * Creates a new {@link Omf51Content} record
	 * 
	 * @param reader A {@link BinaryReader} positioned at the start of the record
	 * @param largeSegmentId True if the segment ID is 2 bytes; false if 1 byte
	 * @throws IOException if an IO-related error occurred
	 */
	public Omf51Content(BinaryReader reader, boolean largeSegmentId) throws IOException {
		super(reader);
		this.largeSegmentId = largeSegmentId;
	}

	@Override
	public void parseData() throws IOException, OmfException {
		segId =
			largeSegmentId ? dataReader.readNextUnsignedShort() : dataReader.readNextUnsignedByte();
		offset = dataReader.readNextUnsignedShort();
		dataIndex = dataReader.getPointerIndex();
		dataSize = (int) (dataEnd - dataIndex);
	}

	/**
	 * {@return the segment ID}
	 */
	public int getSegId() {
		return segId;
	}

	/**
	 * {@return the offset}
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * {@return the data size in bytes}
	 */
	public int getDataSize() {
		return dataSize;
	}

	/**
	 * {@return the start of the data in the reader}
	 */
	public long getDataIndex() {
		return dataIndex;
	}

	@Override
	public DataType toDataType() throws DuplicateNameException, IOException {
		StructureDataType struct = new StructureDataType(Omf51RecordTypes.getName(recordType), 0);
		struct.add(BYTE, "type", null);
		struct.add(WORD, "length", null);
		struct.add(largeSegmentId ? WORD : BYTE, "SEG ID", null);
		struct.add(WORD, "offset", null);
		if (dataSize > 0) {
			struct.add(new ArrayDataType(BYTE, dataSize, 1), "data", null);
		}
		struct.add(BYTE, "checksum", null);

		struct.setCategoryPath(new CategoryPath(OmfUtils.CATEGORY_PATH));
		return struct;
	}
}
