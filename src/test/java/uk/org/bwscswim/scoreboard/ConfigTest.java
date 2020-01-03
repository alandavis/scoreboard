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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.Iterator;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConfigTest
{
    private Config config = new Config(null);

    @Before
    public void before()
    {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void after() throws IOException
    {
    }

    @Test
    public void testGetKey() throws IOException
    {
        for (String key: Config.getKeys("scoreboardName", "state", "name", "attribute.1.2"))
        {
            System.out.println(key);
        }

        Iterator<String> keys;

        // just the attribute
        keys = Config.getKeys(null, null, null, "attribute").iterator();
        assertEquals("attribute", keys.next());
        assertFalse(keys.hasNext());

        // just an attribute with dots that have their most significant part removed.
        keys = Config.getKeys(null, null, null, "lane.foreground").iterator();
        assertEquals("lane.foreground", keys.next());
        assertEquals("foreground", keys.next());
        assertFalse(keys.hasNext());

        // no scoreboard or state
        keys = Config.getKeys(null, null, "name", "attribute").iterator();
        assertEquals("name.attribute", keys.next());
        assertEquals("attribute", keys.next());
        assertFalse(keys.hasNext());

        // no state
        keys = Config.getKeys("scoreboardName", "name", "attribute").iterator();
        assertEquals("scoreboardName.name.attribute", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("scoreboardName.attribute", keys.next());
        assertEquals("name.attribute", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("attribute", keys.next());
        assertFalse(keys.hasNext());

        // no scoreboard
        keys = Config.getKeys(null, "state", "name", "attribute").iterator();
        assertEquals("state.name.attribute", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("state.attribute", keys.next());
        assertEquals("name.attribute", keys.next());
        assertEquals("attribute", keys.next());
        assertFalse(keys.hasNext());

        // all parts. Removes least significant and the repeats having removed most significant.
        keys = Config.getKeys("scoreboardName", "state", "name", "attribute").iterator();
        assertEquals("scoreboardName.state.name.attribute", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("scoreboardName.state.attribute", keys.next());
        assertEquals("scoreboardName.attribute", keys.next());
        assertEquals("state.name.attribute", keys.next());
        assertEquals("state.attribute", keys.next());
        assertEquals("name.attribute", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("attribute", keys.next());
        assertFalse(keys.hasNext());

        keys = Config.getKeys("scoreboard", "state", null, "lane.foreground.color").iterator();
        assertEquals("scoreboard.state.lane.foreground.color", keys.next());
        assertEquals("scoreboard.lane.foreground.color", keys.next());
        assertEquals("state.lane.foreground.color", keys.next());

        assertEquals("scoreboard.state.foreground.color", keys.next());
        assertEquals("scoreboard.foreground.color", keys.next());
        assertEquals("state.foreground.color", keys.next());

        assertEquals("scoreboard.state.color", keys.next());
        assertEquals("scoreboard.color", keys.next());
        assertEquals("state.color", keys.next());

        assertEquals("lane.foreground.color", keys.next());
        assertEquals("foreground.color", keys.next());
        assertEquals("color", keys.next());
        assertFalse(keys.hasNext());
    }
}
