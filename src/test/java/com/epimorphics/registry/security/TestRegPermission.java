/******************************************************************
 * File:        TestRegPermission.java
 * Created by:  Dave Reynolds
 * Created on:  3 Apr 2013
 *
 * (c) Copyright 2013, Epimorphics Limited
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *****************************************************************/

package com.epimorphics.registry.security;

import java.util.Set;

import org.junit.Test;
import static org.junit.Assert.*;

import com.epimorphics.registry.security.RegAction;
import com.epimorphics.registry.security.RegAuthorizationInfo;
import com.epimorphics.registry.security.RegPermission;

public class TestRegPermission {

    @Test
    public void testBasic() {
        RegPermission granted = new RegPermission("Register, Update : /root/reg1");
        assertTrue(granted.getActions().contains( RegAction.Update) );
        assertTrue(granted.getActions().contains( RegAction.Register) );
        assertEquals(2, granted.getActions().size());
        assertEquals("/root/reg1", granted.getPath());

        assertTrue( granted.implies( new RegPermission("Register:/root/reg1") ) );
        assertTrue( granted.implies( new RegPermission("Register:/root/reg1/sub/_item") ) );
        assertTrue( granted.implies( new RegPermission("Update:/root/reg1/sub/_item") ) );
        assertFalse( granted.implies( new RegPermission("Update:/root/reg") ) );
        assertFalse( granted.implies( new RegPermission("Force:/root/reg1") ) );

        // Special case root is "/" not ""
        granted = new RegPermission("Register:/");
        assertTrue( granted.implies( new RegPermission("Register:/root") ) );
    }

    @Test
    public void testWildcard() {
        RegPermission granted = new RegPermission("*:/root/reg1");
        assertTrue( granted.implies( new RegPermission("Register:/root/reg1") ) );
        assertTrue( granted.implies( new RegPermission("Force:/root/reg1") ) );
        assertTrue( granted.implies( new RegPermission("Register:/root/reg1/sub/_item") ) );
    }

    @Test
    public void testGrantAdmin() {
        RegPermission granted = new RegPermission("GrantAdmin");
        assertTrue( granted.implies( new RegPermission("GrantAdmin") ) );
        assertFalse( granted.implies( new RegPermission("Update:/root/reg") ) );

        granted = new RegPermission("*:/root/reg1");
        assertFalse( granted.implies( new RegPermission("GrantAdmin") ) );
    }

    @Test
    public void testResiduals() {
        RegPermission grant1 = new RegPermission("Register:/root/reg1");
        RegPermission grant2 = new RegPermission("Update:/root");
        RegPermission target = new RegPermission("Update,Register:/root/reg1");

        RegPermission r1 = grant1.residual(target);
        assertEquals("Update:/root/reg1", r1.toString());
        assertNull( grant2.residual(r1) );

        // Special case root is "/" not ""
        RegPermission grant3 = new RegPermission("Register,Update:/");
        assertNull( grant3.residual(new RegPermission("Register,Update:/")) );
        assertNull( grant3.residual(new RegPermission("Register,Update:/reg")) );
    }

    @Test
    public void testAuthorizationInfo() {
        RegAuthorizationInfo ai = new RegAuthorizationInfo();
        ai.addObjectPermission( new RegPermission("Register:/root") );
        ai.addObjectPermission( new RegPermission("Update:/root/reg1") );

        assertTrue( ai.permits( new RegPermission("Update,Register:/root/reg1")) );
        assertTrue( ai.permits( new RegPermission("Update,Register:/root/reg1/")) );
        assertTrue( ai.permits( new RegPermission("Update,Register:/root/reg1/item")) );
        assertTrue( ai.permits( new RegPermission("Update:/root/reg1/item")) );
        assertFalse( ai.permits( new RegPermission("Update,Register:/root/reg")) );
        assertFalse( ai.permits( new RegPermission("Update,Register,Force:/root/reg/item")) );

        ai.addRole( "administrator" );
        assertTrue( ai.permits( new RegPermission("Update,Register,Force:/root/reg/item")) );

        // Special case root is "/" not ""
        ai = new RegAuthorizationInfo();
        ai.addObjectPermission( new RegPermission("Register:/") );
        assertTrue( ai.permits( new RegPermission("Register:/root")) );
    }

    @Test
    public void testRoleAliases() {
        RegPermission granted = new RegPermission("Manager:/root/reg1");
        Set<RegAction> actions = granted.getActions();
        assertEquals(4, actions.size());
        assertTrue(actions.contains(RegAction.StatusUpdate));
        assertTrue(actions.contains(RegAction.Update));
        assertTrue(actions.contains(RegAction.Register));
        assertTrue(actions.contains(RegAction.Grant));
    }
}
