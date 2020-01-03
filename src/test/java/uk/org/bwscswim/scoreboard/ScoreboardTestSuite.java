/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2020 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import uk.org.bwscswim.scoreboard.meet.service.ModelHelperTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        ModelHelperTest.class,
        ConfigTest.class,
        DataReaderTest.class,
        PBTest.class,
        ScoreboardTest.class
})

/**
 * @author adavis
 */
public class ScoreboardTestSuite
{
}
