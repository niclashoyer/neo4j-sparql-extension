package de.unikiel.inf.comsys.neo4j.inference.debug;

import java.util.IdentityHashMap;
import java.util.Set;
import org.openrdf.query.algebra.Add;
import org.openrdf.query.algebra.And;
import org.openrdf.query.algebra.ArbitraryLengthPath;
import org.openrdf.query.algebra.Avg;
import org.openrdf.query.algebra.BNodeGenerator;
import org.openrdf.query.algebra.BindingSetAssignment;
import org.openrdf.query.algebra.Bound;
import org.openrdf.query.algebra.Clear;
import org.openrdf.query.algebra.Coalesce;
import org.openrdf.query.algebra.Compare;
import org.openrdf.query.algebra.CompareAll;
import org.openrdf.query.algebra.CompareAny;
import org.openrdf.query.algebra.Copy;
import org.openrdf.query.algebra.Count;
import org.openrdf.query.algebra.Create;
import org.openrdf.query.algebra.Datatype;
import org.openrdf.query.algebra.DeleteData;
import org.openrdf.query.algebra.DescribeOperator;
import org.openrdf.query.algebra.Difference;
import org.openrdf.query.algebra.Distinct;
import org.openrdf.query.algebra.EmptySet;
import org.openrdf.query.algebra.Exists;
import org.openrdf.query.algebra.Extension;
import org.openrdf.query.algebra.ExtensionElem;
import org.openrdf.query.algebra.Filter;
import org.openrdf.query.algebra.FunctionCall;
import org.openrdf.query.algebra.Group;
import org.openrdf.query.algebra.GroupConcat;
import org.openrdf.query.algebra.GroupElem;
import org.openrdf.query.algebra.IRIFunction;
import org.openrdf.query.algebra.If;
import org.openrdf.query.algebra.In;
import org.openrdf.query.algebra.InsertData;
import org.openrdf.query.algebra.Intersection;
import org.openrdf.query.algebra.IsBNode;
import org.openrdf.query.algebra.IsLiteral;
import org.openrdf.query.algebra.IsNumeric;
import org.openrdf.query.algebra.IsResource;
import org.openrdf.query.algebra.IsURI;
import org.openrdf.query.algebra.Join;
import org.openrdf.query.algebra.Label;
import org.openrdf.query.algebra.Lang;
import org.openrdf.query.algebra.LangMatches;
import org.openrdf.query.algebra.LeftJoin;
import org.openrdf.query.algebra.Like;
import org.openrdf.query.algebra.ListMemberOperator;
import org.openrdf.query.algebra.Load;
import org.openrdf.query.algebra.LocalName;
import org.openrdf.query.algebra.MathExpr;
import org.openrdf.query.algebra.Max;
import org.openrdf.query.algebra.Min;
import org.openrdf.query.algebra.Modify;
import org.openrdf.query.algebra.Move;
import org.openrdf.query.algebra.MultiProjection;
import org.openrdf.query.algebra.Namespace;
import org.openrdf.query.algebra.Not;
import org.openrdf.query.algebra.Or;
import org.openrdf.query.algebra.Order;
import org.openrdf.query.algebra.OrderElem;
import org.openrdf.query.algebra.Projection;
import org.openrdf.query.algebra.ProjectionElem;
import org.openrdf.query.algebra.ProjectionElemList;
import org.openrdf.query.algebra.QueryModelNode;
import org.openrdf.query.algebra.QueryRoot;
import org.openrdf.query.algebra.Reduced;
import org.openrdf.query.algebra.Regex;
import org.openrdf.query.algebra.SameTerm;
import org.openrdf.query.algebra.Sample;
import org.openrdf.query.algebra.Service;
import org.openrdf.query.algebra.SingletonSet;
import org.openrdf.query.algebra.Slice;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.Str;
import org.openrdf.query.algebra.Sum;
import org.openrdf.query.algebra.Union;
import org.openrdf.query.algebra.ValueConstant;
import org.openrdf.query.algebra.Var;
import org.openrdf.query.algebra.ZeroLengthPath;
import org.openrdf.query.algebra.helpers.QueryModelVisitorBase;

public class SeenVisitor
		extends QueryModelVisitorBase<RuntimeException> {
	
	private final IdentityHashMap<QueryModelNode, Void> seen;
	
	public SeenVisitor() {
		super();
		this.seen = new IdentityHashMap<>();
	}
	
	private void setSeen(QueryModelNode node) {
		seen.put(node, null);
	}
	
	public Set<QueryModelNode> getSeen() {
		return seen.keySet();
	}

	@Override
	public void meet(QueryRoot node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Add node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(And node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ArbitraryLengthPath node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Avg node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(BindingSetAssignment node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(BNodeGenerator node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Bound node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Clear node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Coalesce node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Compare node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(CompareAll node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(CompareAny node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(DescribeOperator node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Copy node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Count node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Create node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Datatype node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(DeleteData node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Difference node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Distinct node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(EmptySet node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Exists node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Extension node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ExtensionElem node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Filter node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(FunctionCall node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Group node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(GroupConcat node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(GroupElem node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(If node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(In node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(InsertData node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Intersection node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(IRIFunction node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(IsBNode node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(IsLiteral node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(IsNumeric node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(IsResource node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(IsURI node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Join node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Label node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Lang node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(LangMatches node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(LeftJoin node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Like node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Load node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(LocalName node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(MathExpr node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Max node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Min node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Modify node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Move node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(MultiProjection node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Namespace node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Not node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Or node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Order node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(OrderElem node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Projection node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ProjectionElem node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ProjectionElemList node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Reduced node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Regex node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(SameTerm node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Sample node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Service node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(SingletonSet node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Slice node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(StatementPattern node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Str node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Sum node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Union node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ValueConstant node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ListMemberOperator node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(Var node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meet(ZeroLengthPath node) throws RuntimeException {
		setSeen(node);
		super.meet(node);
	}

	@Override
	public void meetOther(QueryModelNode node) throws RuntimeException {
		setSeen(node);
		super.meetOther(node);
	}

}
