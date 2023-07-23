/*
 * Copyright (c) 2002-2023 Gargoyle Software Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.htmlunit.archunit;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.runner.RunWith;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;

/**
 * Architecture tests for our test cases.
 *
 * @author Ronald Brill
 */
@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "org.htmlunit")
public class Architecture2Test {

    @ArchTest
    public static void allPropertyTestShoulsTestTheSameObjects(final JavaClasses classes) {
        compare(classes, "ElementPropertiesTest", "ElementOwnPropertiesTest");
        compare(classes, "ElementPropertiesTest", "ElementOwnPropertySymbolsTest");
    }

    @ArchTest
    public static void allElementTestShoulsTestTheSameObjects(final JavaClasses classes) {
        compare(classes, "ElementChildNodesTest", "ElementClosesItselfTest");
        compare(classes, "ElementChildNodesTest", "ElementCreationTest");
        compare(classes, "ElementChildNodesTest", "ElementDefaultStyleDisplayTest");
        compare(classes, "ElementChildNodesTest", "ElementOuterHtmlTest");
    }

    private static void compare(final JavaClasses classes, final String oneName, final String anotherName) {
        final Set<String> oneTests =
                classes.get("org.htmlunit.general." + oneName).getAllMethods().stream()
                    .filter(m -> m.tryGetAnnotationOfType("org.junit.Test").isPresent())
                    .map(m -> m.getName())
                    .collect(Collectors.toSet());

        final Set<String> anotherTests =
                classes.get("org.htmlunit.general." + anotherName).getAllMethods().stream()
                    .filter(m -> m.tryGetAnnotationOfType("org.junit.Test").isPresent())
                    .map(m -> m.getName())
                    .collect(Collectors.toSet());

        final Set<String> tmp = new HashSet<>(anotherTests);
        tmp.removeAll(oneTests);
        oneTests.removeAll(anotherTests);

        if (tmp.size() + oneTests.size() > 0) {
            if (tmp.size() == 0) {
                oneTests.removeAll(anotherTests);
                Assert.fail("The method(s) " + oneTests
                    + " are available in " + oneName + " but missing in " + anotherName + ".");
            }
            else if (oneTests.size() == 0) {
                anotherTests.removeAll(oneTests);
                Assert.fail("The method(s) " + tmp
                    + " are available in " + anotherName + " but missing in " + oneName + ".");
            }

            Assert.fail("The method(s) " + tmp
                    + " are available in " + anotherName + " but missing in " + oneName
                    + " and the method(s) " + oneTests
                    + " are available in " + oneName + " but missing in " + anotherName + ".");
        }
    }
}
