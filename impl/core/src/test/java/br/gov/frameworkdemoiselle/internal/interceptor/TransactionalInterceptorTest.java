/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.internal.interceptor;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.interceptor.InvocationContext;

import org.easymock.EasyMock;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.frameworkdemoiselle.DemoiselleException;
import br.gov.frameworkdemoiselle.transaction.Transaction;
@Ignore
public class TransactionalInterceptorTest {

	private TransactionalInterceptor interceptor;

	private InvocationContext ic;

	private Transaction transaction;

	class ClassWithMethod {

		public void method() {
			System.out.println("test method");
		}
	}

	// @Before
	// @SuppressWarnings("unchecked")
	// public void setUp() throws Exception {
	// Instance<Transaction> transactionInstance = EasyMock.createMock(Instance.class);
	// Instance<TransactionInfo> transactionContextInstance = EasyMock.createMock(Instance.class);
	//
	// Logger logger = EasyMock.createMock(Logger.class);
	// ResourceBundle bundle = new ResourceBundle("demoiselle-core-bundle", Locale.getDefault());
	// transaction = EasyMock.createMock(Transaction.class);
	// TransactionInfo context = new TransactionInfo();
	//
	// this.interceptor = new TransactionalInterceptor(transactionInstance, transactionContextInstance, logger, bundle);
	// this.ic = EasyMock.createMock(InvocationContext.class);
	//
	// expect(transactionInstance.get()).andReturn(transaction).anyTimes();
	// expect(transactionContextInstance.get()).andReturn(context).anyTimes();
	// expect(this.ic.proceed()).andReturn(null);
	// expect(this.ic.getMethod()).andReturn(ClassWithMethod.class.getMethod("method"));
	// replay(this.ic, transactionInstance, transactionContextInstance);
	// }

	@Test
	public void testManageWithInativeTransactionAndTransactionInterceptorBeginAndDoNotIsMarkedRollback()
			throws Exception {
		expect(this.transaction.isActive()).andReturn(false).times(1);
		expect(this.transaction.isActive()).andReturn(true).times(2);
		expect(this.transaction.isMarkedRollback()).andReturn(false).anyTimes();
		this.transaction.begin();
		this.transaction.commit();
		replay(this.transaction);

		assertEquals(null, this.interceptor.manage(this.ic));
		verify();
	}

	@Test
	public void testManageWithInativeTransactionAndTransactionInterceptorBeginAndIsMarkedRollback() throws Exception {
		expect(this.transaction.isActive()).andReturn(false).times(1);
		expect(this.transaction.isActive()).andReturn(true).times(2);
		expect(this.transaction.isMarkedRollback()).andReturn(true).anyTimes();
		this.transaction.begin();
		this.transaction.rollback();
		replay(this.transaction);

		assertEquals(null, this.interceptor.manage(this.ic));
		verify();
	}

	@Test
	public void testManageWithAtiveTransaction() throws Exception {
		expect(this.transaction.isActive()).andReturn(true).anyTimes();
		replay(this.transaction);

		assertEquals(null, this.interceptor.manage(this.ic));
		verify();
	}

	@Test
	public void testManageWithAtiveTransactionButTheTransactionWasInative() throws Exception {
		expect(this.transaction.isActive()).andReturn(true).times(1);
		expect(this.transaction.isActive()).andReturn(false).times(2);
		replay(this.transaction);

		assertEquals(null, this.interceptor.manage(this.ic));
		verify();
	}

	@Test
	public void testManageWithAtiveTransactionAndMethodThrowExceptionAndDoNotIsMarkedRollback() throws Exception {
		expect(this.transaction.isActive()).andReturn(true).anyTimes();
		expect(this.transaction.isMarkedRollback()).andReturn(false).anyTimes();
		this.transaction.setRollbackOnly();
		replay(this.transaction);

		this.ic = EasyMock.createMock(InvocationContext.class);
		expect(this.ic.proceed()).andThrow(new DemoiselleException(""));
		expect(this.ic.getMethod()).andReturn(ClassWithMethod.class.getMethod("method"));
		replay(this.ic);

		try {
			this.interceptor.manage(this.ic);
			fail();
		} catch (DemoiselleException cause) {
			assertTrue(true);
		}
		verify();
	}

	@Test
	public void testManageWithAtiveTransactionAndMethodThrowExceptionAndIsMarkedRollback() throws Exception {
		expect(this.transaction.isActive()).andReturn(true).anyTimes();
		expect(this.transaction.isMarkedRollback()).andReturn(true).anyTimes();
		this.transaction.setRollbackOnly();
		replay(this.transaction);

		this.ic = EasyMock.createMock(InvocationContext.class);
		expect(this.ic.proceed()).andThrow(new DemoiselleException(""));
		expect(this.ic.getMethod()).andReturn(ClassWithMethod.class.getMethod("method"));
		replay(this.ic);

		try {
			this.interceptor.manage(this.ic);
			fail();
		} catch (DemoiselleException cause) {
			assertTrue(true);
		}
		verify();
	}
}
