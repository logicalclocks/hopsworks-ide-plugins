/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.logicalclocks.upload.params;

public class FlowParameter {

    private long flowChunkNumber; //starts with 1 not 0
    private long flowChunkSize;
    private long flowCurrentChunkSize;
    private long flowTotalSize;
    private String flowIdentifier;
    private String flowFilename;
    private String flowRelativePath;
    private long flowTotalChunks;

    public long getFlowChunkNumber() {
        return flowChunkNumber;
    }

    public long getFlowChunkSize() {
        return flowChunkSize;
    }

    public long getFlowCurrentChunkSize() {
        return flowCurrentChunkSize;
    }

    public long getFlowTotalChunks() {
        return flowTotalChunks;
    }

    public long getFlowTotalSize() {
        return flowTotalSize;
    }

    public String getFlowFilename() {
        return flowFilename;
    }

    public String getFlowIdentifier() {
        return flowIdentifier;
    }

    public String getFlowRelativePath() {
        return flowRelativePath;
    }

    public void setFlowChunkNumber(long flowChunkNumber) {
        this.flowChunkNumber = flowChunkNumber;
    }

    public void setFlowChunkSize(long flowChunkSize) {
        this.flowChunkSize = flowChunkSize;
    }

    public void setFlowCurrentChunkSize(long flowCurrentChunkSize) {
        this.flowCurrentChunkSize = flowCurrentChunkSize;
    }

    public void setFlowFilename(String flowFilename) {
        this.flowFilename = flowFilename;
    }

    public void setFlowIdentifier(String flowIdentifier) {
        this.flowIdentifier = flowIdentifier;
    }

    public void setFlowRelativePath(String flowRelativePath) {
        this.flowRelativePath = flowRelativePath;
    }

    public void setFlowTotalChunks(long flowTotalChunks) {
        this.flowTotalChunks = flowTotalChunks;
    }

    public void setFlowTotalSize(long flowTotalSize) {
        this.flowTotalSize = flowTotalSize;
    }
}
