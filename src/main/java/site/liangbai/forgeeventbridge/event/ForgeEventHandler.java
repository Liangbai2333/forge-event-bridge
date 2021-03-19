/*
 * Forge-Event-Bridge
 * Copyright (C) 2021  Liangbai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package site.liangbai.forgeeventbridge.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ForgeEventHandler {
    Target value();

    EventPriority priority() default EventPriority.NORMAL;

    Bus[] bus() default Bus.FORGE;

    boolean receiveCanceled() default false;

    @interface Target {
        Class<?> value() default None.class;

        /**
         * 例如: com.craftingdead.core.event.GunEvent$HitBlock
         *
         * @return 类文件名称
         */
        String source() default "";
    }

    /**
     * 仅仅用于标记不通过value属性来查找监听目标类.
     *
     * @author Liangbai
     */
    class None { }

    enum Bus {
        FORGE,
        MOD
    }
}
