package hu.blackbelt.epsilon.runtime.execution.exceptions;

/*-
 * #%L
 * epsilon-runtime-execution
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
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
 * #L%
 */


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.epsilon.evl.execute.UnsatisfiedConstraint;

import java.util.List;
import java.util.Set;

@Getter
public class EvlScriptExecutionException extends ScriptExecutionException {

    @Builder.Default
    String message = "";

    @Builder.Default
    Set<String> errorsNotFound = ImmutableSet.of();

    @Builder.Default
    Set<String> warningsNotFound = ImmutableSet.of();

    @Builder.Default
    Set<String> unexpectedErrors = ImmutableSet.of();

    @Builder.Default
    Set<String> unexpectedWarnings = ImmutableSet.of();

    @Builder.Default
    List<UnsatisfiedConstraint> unsatisfiedErrors = ImmutableList.of();

    @Builder.Default
    List<UnsatisfiedConstraint> unsatisfiedWarnings = ImmutableList.of();

    @Builder(builderMethodName = "evlScriptExecutionExceptionBuilder")
    @java.beans.ConstructorProperties({"message", "errorsNotFound", "warningsNotFound", "unexpectedErrors",
            "unexpectedWarnings", "unsatisfiedErrors", "unsatisfiedWarnings"})
    public EvlScriptExecutionException(String message,
                                       Set<String> errorsNotFound,
                                       Set<String> warningsNotFound,
                                       Set<String> unexpectedErrors,
                                       Set<String> unexpectedWarnings,
                                       List<UnsatisfiedConstraint> unsatisfiedErrors,
                                       List<UnsatisfiedConstraint> unsatisfiedWarnings) {
        this.message = message;
        if (errorsNotFound != null) this.errorsNotFound = errorsNotFound;
        if (warningsNotFound != null) this.warningsNotFound = warningsNotFound;
        if (unexpectedErrors != null) this.unexpectedErrors = unexpectedErrors;
        if (unexpectedWarnings != null) this.unexpectedWarnings = unexpectedWarnings;
        if (unsatisfiedErrors != null) this.unsatisfiedErrors = unsatisfiedErrors;
        if (unsatisfiedWarnings != null) this.unsatisfiedWarnings = unsatisfiedWarnings;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();

        if (unsatisfiedErrors.size() > 0 || unsatisfiedWarnings.size() > 0) {
            printErrors(stringBuffer);
            stringBuffer.append("\t" + "\n");
            printWarnings(stringBuffer);
        } else {
            stringBuffer.append("All constraints have been satisfied");
        }
        return stringBuffer.toString();
    }

    @Override
    public String getMessage() {
        return message + "\n" +
                "" +
                "" + toString();
    }

    private void printErrors(StringBuffer stringBuffer) {
        stringBuffer.append("\t" + unsatisfiedErrors.size() + " error(s) \n");
        for (UnsatisfiedConstraint uc : unsatisfiedErrors) {
            stringBuffer.append("\t" + uc.getMessage() + "\n");
        }
    }

    private void printWarnings(StringBuffer stringBuffer) {
        stringBuffer.append("\t" + unsatisfiedWarnings.size() + " warning(s) \n");
        for (UnsatisfiedConstraint uc : unsatisfiedWarnings) {
            stringBuffer.append("\t" + uc.getMessage() + "\n");
        }
    }

}

