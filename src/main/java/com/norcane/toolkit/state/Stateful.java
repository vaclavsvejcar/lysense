/*
 * lysense :: license header manager
 * Copyright (c) 2023-2024 Vaclav Svejcar
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.norcane.toolkit.state;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

/**
 * Represents an <i>stateful</i> object which can be reset to its initial state. If the state composes of other stateful objects, such objects can register
 * themselves to the <i>state context</i> and when the parent object is reset, all registered objects will be reset as well.
 *
 * @see Context
 */
public interface Stateful {

    /**
     * Returns the state context of this object.
     *
     * @return state context
     */
    default Context stateContext() {
        return Context.of(this);
    }

    /**
     * Resets the state of this object and all child objects registered to the <i>state context</i> to their initial state.
     */
    default void resetState() {
        stateContext().reset();
    }

    /**
     * Represents a context of the stateful object. It holds a list of all registered child objects and when the context is reset, all registered objects are
     * reset.
     */
    class Context {
        private static final WeakHashMap<Object, Context> contexts = new WeakHashMap<>();

        private final int id;
        private final List<Stateful> registered = new ArrayList<>();

        private Context(int id) {
            this.id = id;
        }

        /**
         * Returns the state context of the given object.
         *
         * @param object object
         * @return state context
         */
        private static Context of(Object object) {
            return contexts.computeIfAbsent(object, _ -> new Context(object.hashCode()));
        }

        /**
         * Registers the given <i>stateful</i> object to this context.
         *
         * @param stateful object
         */
        public void register(Stateful stateful) {
            registered.add(stateful);
        }

        /**
         * Resets the state of all registered objects to their initial state.
         */
        public void reset() {
            registered.forEach(Stateful::resetState);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("registered", registered)
                .toString();
        }
    }
}
