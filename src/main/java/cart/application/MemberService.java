package cart.application;

import cart.domain.Member;
import cart.dto.request.MemberCreateRequest;
import cart.dto.response.MemberCreateResponse;
import cart.dto.response.MemberPointQueryResponse;
import cart.dto.response.MemberQueryResponse;
import cart.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private static final int JOIN_EVENT_POINT = 5000;
    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberCreateResponse join(final MemberCreateRequest request) {
        final Member member = memberRepository.addMember(new Member(request.getEmail(), request.getPassword()), JOIN_EVENT_POINT);
        return MemberCreateResponse.from(member);
    }

    public MemberPointQueryResponse findPointsOf(final Member member) {
        return new MemberPointQueryResponse(memberRepository.findPointOf(member));
    }

    public void addPoints(final Member member, final int points) {
        memberRepository.addPoint(member, points);
    }

    public List<MemberQueryResponse> findAllMembers() {
        final List<Member> members = memberRepository.findAllMembers();
        return members.stream()
                .map(MemberQueryResponse::from)
                .collect(Collectors.toList());
    }

    public MemberQueryResponse findMemberById(final Long id) {
        return MemberQueryResponse.from(memberRepository.findMemberById(id));
    }
}
