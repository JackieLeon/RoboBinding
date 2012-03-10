/**
 * Copyright 2012 Cheng Wei, Robert Taylor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.robobinding.binder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.robobinding.binder.ViewPendingAttributes.AttributeGroupResolver;
import org.robobinding.binder.ViewPendingAttributes.AttributeResolver;

import android.view.View;

import com.google.common.collect.Maps;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Cheng Wei
 */
public class ViewPendingAttributesImplTest
{
	private AttributeResolverImpl attributeResolver;
	
	@Before
	public void setUp()
	{
		attributeResolver = new AttributeResolverImpl();
	}
	
	@Test
	public void givenAttributeInPendingList_whenResolveAttributeIfExists_thenResolved()
	{
		String attribute = "existingAttribute";
		ViewPendingAttributes viewPendingAttributes = createWithPendingList(attribute);
		
		viewPendingAttributes.resolveAttributeIfExists(attribute, attributeResolver);
		
		assertTrue(attributeResolver.isAttributeResolved(attribute));
	}
	
	@Test
	public void givenAttributeNotInPendingList_whenResolveAttributeIfExists_thenIgnore()
	{
		String attribute = "nonExistingAttribute";
		ViewPendingAttributes viewPendingAttributes = createWithPendingList();
		
		viewPendingAttributes.resolveAttributeIfExists(attribute, attributeResolver);
		
		assertFalse(attributeResolver.isAttributeResolved(attribute));
	}
	
	@Test
	public void whenResolveAttributeSuccessfully_thenAttributeRemovedOffPendingList()
	{
		String attribute = "existingAttribute";
		ViewPendingAttributes viewPendingAttributes = createWithPendingList(attribute);
		
		viewPendingAttributes.resolveAttributeIfExists(attribute, mock(AttributeResolver.class));

		assertTrue(viewPendingAttributes.isEmpty());
	}
	
	@Test
	public void whenResolveAttributeFailed_thenAttributeRemovedOffPendingList()
	{
		String attribute = "existingAttribute";
		ViewPendingAttributes viewPendingAttributes = createWithPendingList(attribute);
		AttributeResolver attributeResolver = mock(AttributeResolver.class);
		when(attributeResolver).thenThrow(new MalformedBindingAttributeException(""));
		
		viewPendingAttributes.resolveAttributeIfExists(attribute, attributeResolver);

		assertTrue(viewPendingAttributes.isEmpty());
	}
	
	@Test
	public void whenAttributeGroupResolvedSuccessfully_thenPresentAttributesRemovedOffThePendingList()
	{
		String[] presentAttributes = {"group_attribute1", "group_attribute2"};
		String[] attributeGroup = ArrayUtils.add(presentAttributes, "group_attribute3");
		ViewPendingAttributes viewPendingAttributes = createWithPendingList(presentAttributes);
		
		viewPendingAttributes.resolveAttributeGroupIfExists(attributeGroup, mock(AttributeGroupResolver.class));
		
		assertTrue(viewPendingAttributes.isEmpty());
	}
	
	private ViewPendingAttributes createWithPendingList(String... pendingAttributes)
	{
		Map<String, String> pendingAttributeMappings = Maps.newHashMap();
		for(String attribute : pendingAttributes)
		{
			pendingAttributeMappings.put(attribute, "attributeValue");
		}
		
		return new ViewPendingAttributesImpl(mock(View.class), pendingAttributeMappings);
	}

	private class AttributeResolverImpl implements AttributeResolver
	{
		private boolean resolved;
		private String resolvedAttribute;
		
		@Override
		public void resolve(View view, String attribute, String attributeValue)
		{
			resolved = true;
			resolvedAttribute = attribute;
		}
		
		public boolean isAttributeResolved(String attribute)
		{
			if(resolved)
			{
				return StringUtils.equals(resolvedAttribute, attribute);
			}else
			{
				return false;
			}
		}
	}
	
}
