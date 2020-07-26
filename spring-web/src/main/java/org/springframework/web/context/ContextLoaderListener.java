/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 在【spring-web】中的类。
 *
 * ContextLoaderListener 是一个启动类监听器：初始化或销毁 {@link WebApplicationContext}。
 * 简单地委托给 {@link ContextLoader} 和 {@link ContextCleanupListener}。
 *
 * 在 Spring3.1 中，ContextLoaderListener 支持通过 {@link #ContextLoaderListener(WebApplicationContext)}
 * 的构造函数来注入【根web程序应用上下文】，允许在 Servlet 3.0+ 环境中编程配置。
 *
 * 详情见 {@link org.springframework.web.WebApplicationInitializer} 查看使用示例。
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 17.02.2003
 * @see #setContextInitializers
 * @see org.springframework.web.WebApplicationInitializer
 */
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

	/**
	 * 创建一个新的 ContextLoaderListener, 他将基于 "上下文类" 和 "上下文配置"
	 * 这两个servlet上下文参数, 创建一个 web应用程序上下文。
	 *
	 * 详情见 See {@link ContextLoader}, 了解每个默认值的详细信息。
	 *
	 * 通常使用该构造函数来声明 web.xml 中的 <listener></listener> 配置。
	 *
	 * 创建出来的应用程序上下文，将以 {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 这个为注册的名字，被注册到 ServletContext。当调用该类的 {@link #contextDestroyed} 的声明周期方法，
	 * Spring 应用程序上下文将会被关闭。
	 *
	 * @see ContextLoader
	 * @see #ContextLoaderListener(WebApplicationContext)
	 * @see #contextInitialized(ServletContextEvent)
	 * @see #contextDestroyed(ServletContextEvent)
	 */
	public ContextLoaderListener() {
	}

	/**
	 * 创建一个新的 ContextLoaderListener, 他将基于给定的上下文，创建一个 web应用程序上下文。
	 * 这个构造函数在Servlet 3.0+环境中非常有用。
	 * 通过 {@link javax.servlet.ServletContext#addListener}，基于实例的监听器注册是可能的。
	 *
	 * 提供的上下文可能已经调用，也可能还没调用 ConfigurableApplicationContext#refresh() 的方法进行刷新.
	 * 如果它是 {@link ConfigurableWebApplicationContext} 的实现类，并且没被刷新，那么将会发生以下情况：
	 *
	 * 1、如果给定的上下文没通过 ConfigurableApplicationContext#setId 来分配id，那么就会为它分配一个.
	 *
	 * 2、ServletContext 和 ServletConfig 对象将被委托给这个应用上下文，{@link #customizeContext} 也将会被调用。
	 *
	 * 3、被 “contextInitializerClasses” 的 init-param 中指定的任何 ApplicationContextInitializer 将会被应用。
	 *
	 * 4、最后调用 ConfigurableApplicationContext#refresh 的刷新方法。
	 *
	 * <p>The context may or may not yet be {@linkplain
	 * org.springframework.context.ConfigurableApplicationContext#refresh() refreshed}. If it
	 * (a) is an implementation of {@link ConfigurableWebApplicationContext} and
	 * (b) has <strong>not</strong> already been refreshed (the recommended approach),
	 * then the following will occur:
	 * <ul>
	 * <li>If the given context has not already been assigned an {@linkplain
	 * org.springframework.context.ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
	 * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to
	 * the application context</li>
	 * <li>{@link #customizeContext} will be called</li>
	 * <li>Any {@link org.springframework.context.ApplicationContextInitializer ApplicationContextInitializer}s
	 * specified through the "contextInitializerClasses" init-param will be applied.</li>
	 * <li>{@link org.springframework.context.ConfigurableApplicationContext#refresh refresh()} will be called</li>
	 * </ul>
	 *
	 * 如果该上下文已经调用刷新方法，或者不是 ConfigurableWebApplicationContext 的实现类，那么上述操作将不会进行。
	 *
	 * 详情查看 {@link org.springframework.web.WebApplicationInitializer} 的示例用法.
	 *
	 * 创建出来的应用程序上下文，将以 {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * 这个为注册的名字，被注册到 ServletContext。当调用该类的 {@link #contextDestroyed} 的声明周期方法，
	 * Spring 应用程序上下文将会被关闭。
	 *
	 * @param context the application context to manage
	 * @see #contextInitialized(ServletContextEvent)
	 * @see #contextDestroyed(ServletContextEvent)
	 */
	public ContextLoaderListener(WebApplicationContext context) {
		super(context);
	}


	/**
	 * 初始化【根web应用上下文】
	 * 调用的是父类的 initWebApplicationContext()，来初始化一个 WebApplicationContext。
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		initWebApplicationContext(event.getServletContext());
	}


	/**
	 * 销毁【根web应用上下文】
	 * 调用的是父类的 closeWebApplicationContext(),
	 * 还有调用 ContextCleanupListener#cleanupAttributes 来清除上下文的参数.
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		closeWebApplicationContext(event.getServletContext());
		ContextCleanupListener.cleanupAttributes(event.getServletContext());
	}

}
