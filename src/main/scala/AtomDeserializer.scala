package atomflow

import com.gu.contentatom.thrift.ContentAtomEvent
import com.gu.contentatom.thrift.util.ThriftDeserializer

object AtomDeserializer extends ThriftDeserializer[ContentAtomEvent] {
  val codec = ContentAtomEvent
}
